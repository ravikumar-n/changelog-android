package com.ravikumar.changelogmonitor.features.subscriptions.billing

import android.app.Activity
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponse
import com.android.billingclient.api.BillingClient.FeatureType
import com.android.billingclient.api.BillingClient.SkuType
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.Purchase.PurchasesResult
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.SkuDetailsParams
import com.ravikumar.changelogmonitor.BuildConfig
import timber.log.Timber
import java.io.IOException

/**
 * Handles all the interactions with Play Store (via Billing library), maintains connection to
 * it through BillingClient and caches temporary states/data if needed
 */
class BillingManager(
  activity: Activity,
  updatesListener: BillingUpdatesListener
) : PurchasesUpdatedListener {
  // region Variables
  /** A reference to BillingClient  */
  private var billingClient: BillingClient? = null

  /**
   * True if billing service is connected now.
   */
  private var isServiceConnected: Boolean = false
  private var billingUpdatesListener: BillingUpdatesListener? = null
  private var activity: Activity? = null
  private val purchases: MutableList<Purchase> = ArrayList()
  private var billingClientResponseCode = BILLING_MANAGER_NOT_INITIALIZED
  private val mPurchases = mutableListOf<Purchase>()
  // endregion

  // region Init
  init {
    this.activity = activity
    billingUpdatesListener = updatesListener
    billingClient = BillingClient.newBuilder(activity)
      .setListener(this)
      .build()

    // Start setup. This is asynchronous and the specified listener will be called
    // once setup completes.
    // It also starts to report all the new purchases through onPurchasesUpdated() callback.
    startServiceConnection(
      Runnable {
        // Notifying the listener that billing client is ready
        billingUpdatesListener?.onBillingClientSetupFinished()
        // IAB is fully set up. Now, let's get an inventory of stuff we own.
        Timber.d("Setup successful. Querying inventory.")
        queryPurchases()
      }
    )
  }
  // endregion

  // region Public
  /**
   * Returns the value Billing client response code or BILLING_MANAGER_NOT_INITIALIZED if the
   * client connection response was not received yet.
   */
  fun getBillingClientResponseCode(): Int {
    return billingClientResponseCode
  }

  /**
   * Checks if subscriptions are supported for current client
   *
   * Note: This method does not automatically retry for RESULT_SERVICE_DISCONNECTED.
   * It is only used in unit tests and after queryPurchases execution, which already has
   * a retry-mechanism implemented.
   *
   */
  private fun areSubscriptionsSupported(): Boolean {
    val responseCode = billingClient?.isFeatureSupported(FeatureType.SUBSCRIPTIONS)
    if (responseCode != BillingResponse.OK) {
      Timber.d("areSubscriptionsSupported() got an error response: %d", responseCode)
    }
    return responseCode == BillingResponse.OK
  }

  /**
   * Start a purchase or subscription replace flow
   */
  fun initiatePurchaseFlow(
    skuId: String,
    oldSkus: ArrayList<String>? = null,
    @SkuType billingType: String
  ) {
    val purchaseFlowRequest = Runnable {
      val purchaseParams = BillingFlowParams.newBuilder()
        .setSku(skuId)
        .setType(billingType)
        .setOldSkus(oldSkus)
        .build()
      billingClient?.launchBillingFlow(activity, purchaseParams)
    }

    executeServiceRequest(purchaseFlowRequest)
  }

  /**
   * Clear the resources
   */
  fun destroy() {
    Timber.d("Destroying the billing manager.")

    if (billingClient != null && billingClient?.isReady == true) {
      billingClient?.endConnection()
    }
    billingClient = null
  }
  // endregion

  private fun startServiceConnection(executeOnSuccess: Runnable?) {
    billingClient?.startConnection(
      object : BillingClientStateListener {
        override fun onBillingSetupFinished(@BillingResponse billingResponseCode: Int) {
          Timber.d("Setup finished. Response code: %d", billingResponseCode)

          if (billingResponseCode == BillingResponse.OK) {
            isServiceConnected = true
            executeOnSuccess?.run()
          }
          billingClientResponseCode = billingResponseCode
        }

        override fun onBillingServiceDisconnected() {
          isServiceConnected = false
        }
      }
    )
  }

  override fun onPurchasesUpdated(
    resultCode: Int,
    purchases: MutableList<Purchase>?
  ) {
    when (resultCode) {
      BillingResponse.OK -> {
        purchases?.forEach { purchase ->
          handlePurchase(purchase)
        }
        billingUpdatesListener?.onPurchasesUpdated(mPurchases)
      }
      BillingResponse.USER_CANCELED -> Timber.d(
        "onPurchasesUpdated() - user cancelled the purchase flow - skipping"
      )
      else -> Timber.d("onPurchasesUpdated() got unknown resultCode: %d", resultCode)
    }
  }

  // region Private

  private fun queryPurchases() {
    val queryToExecute = Runnable {
      val purchasesResult = billingClient?.queryPurchases(SkuType.INAPP)

      // If there are subscriptions supported, we add subscription rows as well
      if (areSubscriptionsSupported()) {
        val subscriptionResult = billingClient?.queryPurchases(SkuType.SUBS)
        Timber.d(
          "Querying subscriptions result code: "
            + subscriptionResult?.responseCode
            + " res: " + subscriptionResult?.purchasesList?.size
        )

        if (subscriptionResult?.responseCode == BillingResponse.OK) {
          purchasesResult?.purchasesList?.addAll(
            subscriptionResult.purchasesList
          )
        } else {
          Timber.e("Got an error response trying to query subscription purchases")
        }
      } else if (purchasesResult?.responseCode == BillingResponse.OK) {
        Timber.i("Skipped subscription purchases query since they are not supported")
      } else {
        Timber.i(
          "queryPurchases() got an error response code: " + purchasesResult?.responseCode
        )
      }
      onQueryPurchasesFinished(purchasesResult)
    }
    executeServiceRequest(queryToExecute)
  }

  private fun executeServiceRequest(runnable: Runnable) {
    if (isServiceConnected) {
      runnable.run()
    } else {
      // If billing service was disconnected, we try to reconnect 1 time.
      // (feel free to introduce your retry policy here).
      startServiceConnection(runnable)
    }
  }

  /**
   * Handle a result from querying of purchases and report an updated list to the listener
   */
  private fun onQueryPurchasesFinished(result: PurchasesResult?) {
    // Have we been disposed of in the meantime? If so, or bad result code, then quit
    if (billingClient == null || result?.responseCode != BillingResponse.OK) {
      Timber.d(
        "Billing client was null or result code (" + result?.responseCode
          + ") was bad - quitting"
      )
      return
    }

    Timber.d("Query inventory was successful.")

    // Update the UI and purchases inventory with new list of purchases
    purchases.clear()
    onPurchasesUpdated(BillingResponse.OK, result.purchasesList)
  }

  fun querySkuDetails(skuList: List<String>) {
    val params = SkuDetailsParams.newBuilder()
    params.setSkusList(skuList)
      .setType(SkuType.SUBS)
    billingClient?.querySkuDetailsAsync(
      params.build()
    ) { responseCode, skuDetailsList ->
      billingUpdatesListener?.onSkuDetailsRetrieved(responseCode, skuDetailsList)
    }
  }

  /**
   * Handles the purchase
   *
   * Note: Notice that for each purchase, we check if signature is valid on the client.
   * It's recommended to move this check into your backend.
   * See [Security.verifyPurchase]
   *
   * @param purchase Purchase to be handled
   */
  private fun handlePurchase(purchase: Purchase) {
    if (!verifyValidSignature(purchase.originalJson, purchase.signature)) {
      Timber.d("Got a purchase: %s; but signature is bad. Skipping...", purchase)
      return
    }

    Timber.d("Got a verified purchase: %s", purchase)
    mPurchases.add(purchase)
  }

  /**
   * Verifies that the purchase was signed correctly for this developer's public key.
   *
   * Note: It's strongly recommended to perform such check on your backend since hackers can
   * replace this method with "constant true" if they decompile/rebuild your app.
   *
   */
  private fun verifyValidSignature(
    signedData: String,
    signature: String
  ) = try {
    Security.verifyPurchase(BASE_64_ENCODED_PUBLIC_KEY, signedData, signature)
  } catch (e: IOException) {
    Timber.e(e, "Got an exception trying to validate a purchase: ")
    false
  }
  // endregion

  // region Static
  companion object {
    // region Constants
    // Default value of billingClientResponseCode until BillingManager was not yeat initialized
    private const val BILLING_MANAGER_NOT_INITIALIZED = -1

    /* BASE_64_ENCODED_PUBLIC_KEY should be YOUR APPLICATION'S PUBLIC KEY
     * (that you got from the Google Play developer console). This is not your
     * developer public key, it's the *app-specific* public key.
     *
     * Instead of just storing the entire literal string here embedded in the
     * program,  construct the key at runtime from pieces or
     * use bit manipulation (for example, XOR with some other string) to hide
     * the actual key.  The key itself is not secret information, but we don't
     * want to make it easy for an attacker to replace the public key with one
     * of their own and then fake messages from the server.
     */
    private const val BASE_64_ENCODED_PUBLIC_KEY = BuildConfig.LICENSE_KEY
    // endregion
  }
  // endregion
}


