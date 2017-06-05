package com.ravikumar.changelogmonitor.features.subscriptions

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.support.constraint.Group
import android.support.v7.app.AlertDialog
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.billingclient.api.BillingClient.BillingResponse
import com.android.billingclient.api.BillingClient.SkuType
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.ravikumar.changelogmonitor.BuildConfig
import com.ravikumar.changelogmonitor.ChangelogApplication
import com.ravikumar.changelogmonitor.R
import com.ravikumar.changelogmonitor.SKU_HALF_YEARLY
import com.ravikumar.changelogmonitor.SKU_YEARLY
import com.ravikumar.changelogmonitor.features.subscriptions.billing.BillingManager
import com.ravikumar.changelogmonitor.features.subscriptions.billing.BillingUpdatesListener
import com.ravikumar.changelogmonitor.framework.extensions.bindColor
import com.ravikumar.changelogmonitor.framework.extensions.statusBarHeight
import com.ravikumar.changelogmonitor.helpers.events.SubscriptionChangeEvent
import com.ravikumar.changelogmonitor.helpers.events.UserEvent
import com.ravikumar.changelogmonitor.ui.ChangelogProgressDialog
import com.ravikumar.entities.ChangelogSubscription
import com.ravikumar.entities.SubscriptionResponse
import com.ravikumar.entities.User
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_subscriptions.freeGroup
import kotlinx.android.synthetic.main.activity_subscriptions.halfYearlyGroup
import kotlinx.android.synthetic.main.activity_subscriptions.halfYearlyLayout
import kotlinx.android.synthetic.main.activity_subscriptions.planHalfYearlyPriceText
import kotlinx.android.synthetic.main.activity_subscriptions.planYearlyPriceText
import kotlinx.android.synthetic.main.activity_subscriptions.subscriptionToolbar
import kotlinx.android.synthetic.main.activity_subscriptions.yearlyGroup
import kotlinx.android.synthetic.main.activity_subscriptions.yearlyLayout
import timber.log.Timber
import javax.inject.Inject

class SubscriptionActivity : DaggerAppCompatActivity(),
  SubscriptionContract.View,
  BillingUpdatesListener {
  // region DI
  @Inject lateinit var presenter: SubscriptionsPresenter<SubscriptionContract.View>
  // endregion

  // region Variables
  private val subscriptionColor by bindColor(R.color.subscription)
  private val selectedSubscriptionColor by bindColor(R.color.subscriptionSelected)
  private var billingManager: BillingManager? = null
  // endregion

  // region Lifecycle
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_subscriptions)
    setupToolbar()
    presenter.onAttach(this)
    setupBilling()
    setListeners()
  }

  override fun onActivityResult(
    requestCode: Int,
    resultCode: Int,
    data: Intent?
  ) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == RC_SIGN_IN) { //
      val task = GoogleSignIn.getSignedInAccountFromIntent(data)
      if (task.isSuccessful) {
        task.result.let {
          presenter.connectUser(it.idToken!!, it.displayName!!, it.email!!)
        }
      } else {
        Toast.makeText(this, R.string.error_google_sign_in_failed, Toast.LENGTH_LONG)
          .show()
      }
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    presenter.onDetach()
    billingManager?.destroy()
    billingManager = null
  }
  // endregion

  // region View
  override fun showLoading(textRes: Int) {
    ChangelogProgressDialog.showDialog(this, textRes)
  }

  override fun hideLoading() {
    ChangelogProgressDialog.hideDialog(this)
  }

  override fun showEmptyState(
    drawableRes: Int,
    textRes: Int
  ) {
  }

  override fun showErrorState(
    drawableRes: Int,
    textRes: Int
  ) {
    Toast.makeText(this, textRes, Toast.LENGTH_SHORT)
      .show()
  }

  override fun onSubscriptionPurchased(subscriptionResponse: SubscriptionResponse) {
    ChangelogApplication.rxBus.send(SubscriptionChangeEvent())
  }

  override fun onSubscriptionRemoved() {
    ChangelogApplication.rxBus.send(SubscriptionChangeEvent())
  }

  override fun showFreePlan() {
    enablePlanGroup(freeGroup)
    disabledPlansGroup(groups = arrayOf(halfYearlyGroup, yearlyGroup), isEnabled = true)
  }

  override fun showSubscription(subscription: ChangelogSubscription) {
    with(subscription) {
      if (this.productId == com.ravikumar.changelogmonitor.SKU_YEARLY) {
        enablePlanGroup(yearlyGroup)
        disabledPlansGroup(groups = arrayOf(freeGroup, halfYearlyGroup))
      } else if (this.productId == com.ravikumar.changelogmonitor.SKU_HALF_YEARLY) {
        enablePlanGroup(halfYearlyGroup)
        disabledPlansGroup(groups = arrayOf(freeGroup, yearlyGroup))
      }
    }
  }

  override fun onFailedToRegisterSubscription(error: Pair<Int, String?>) {
    Toast.makeText(this, error.second ?: getString(error.first), Toast.LENGTH_LONG)
      .show()
    enablePlanGroup(freeGroup)
    disabledPlansGroup(groups = arrayOf(halfYearlyGroup, yearlyGroup), isEnabled = true)
  }

  override fun onGoogleSignIn(userName: String) {
    ChangelogProgressDialog.showDialog(this, getString(R.string.progress_s_connect_user, userName))
  }

  override fun onGoogleSignInSuccess(user: User) {
    ChangelogProgressDialog.hideDialog(this)
    Toast.makeText(
      this, getString(R.string.success_s_account_connected, user.givenName),
      Toast.LENGTH_LONG
    )
      .show()
    ChangelogApplication.rxBus.send(UserEvent())
  }

  override fun onGoogleSignInFailed() {
    ChangelogProgressDialog.hideDialog(this)
    Toast.makeText(this, R.string.error_failed_to_connect_account, Toast.LENGTH_LONG)
      .show()
  }
  // endregion

  // region Overrides
  override fun onBillingClientSetupFinished() {
    if (!isFinishing) {
      billingManager?.querySkuDetails(listOf(SKU_HALF_YEARLY, SKU_YEARLY))
    }
  }

  override fun onPurchasesUpdated(purchases: List<Purchase>) {
    if (purchases.isNotEmpty()) {
      val purchase: Purchase = purchases[0]
      presenter.checkAndRegisterPurchase(purchase)
    } else {
      disabledPlansGroup(groups = arrayOf(halfYearlyGroup, yearlyGroup), isEnabled = true)
      presenter.removeSubscriptionsIfAny()
    }
  }

  override fun onSkuDetailsRetrieved(
    responseCode: Int,
    skuDetailsList: MutableList<SkuDetails>?
  ) {
    if (responseCode == BillingResponse.OK && skuDetailsList != null) {
      for (skuDetails in skuDetailsList) {
        Timber.d("skuDetails : %s", skuDetails)
        val sku = skuDetails.sku
        // TODO: Add some price fetching animation text
        if (sku == SKU_HALF_YEARLY) {
          planHalfYearlyPriceText.text = skuDetails.price
        } else if (sku == SKU_YEARLY) {
          planYearlyPriceText.text = skuDetails.price
        }
      }
    }
  }
  // endregion

  // region Private
  private fun setupToolbar() {
    setSupportActionBar(subscriptionToolbar)
    supportActionBar?.title = getString(R.string.title_subscription)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    subscriptionToolbar.setPadding(0, statusBarHeight(), 0, 0)
  }

  private fun setupBilling() {
    billingManager = BillingManager(this, this)
  }

  private fun setListeners() {
    halfYearlyLayout.setOnClickListener {
      if (presenter.isGuestUser()) {
        showGuestUserAlert()
      } else {
        billingManager?.initiatePurchaseFlow(
          skuId = SKU_HALF_YEARLY,
          billingType = SkuType.SUBS
        )
      }
    }

    yearlyLayout.setOnClickListener {
      if (presenter.isGuestUser()) {
        showGuestUserAlert()
      } else {
        billingManager?.initiatePurchaseFlow(skuId = SKU_YEARLY, billingType = SkuType.SUBS)
      }
    }
  }

  @SuppressLint("NewApi")
  private fun showGuestUserAlert() {
    val builder = with(AlertDialog.Builder(this, R.style.ChangelogAlertDialog)) {
      setMessage(R.string.prompt_user_sign_with_google)
      setPositiveButton(
        R.string.dialog_login, { _, _ ->
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
          .requestIdToken(BuildConfig.SERVER_CLIENT_ID)
          .requestEmail()
          .build()

        val signInClient = GoogleSignIn.getClient(this@SubscriptionActivity, options)
        signInClient?.signOut()
        signInClient?.signInIntent?.let {
          startActivityForResult(it, RC_SIGN_IN)
        }
      }
      )
      setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
    }

    val dialog = builder.create()
      .apply {
        setTitle(R.string.title_subscription)
        setCancelable(true)
        show()
      }

    val messageText = dialog.findViewById(android.R.id.message) as? TextView
    messageText?.run {
      if (Build.VERSION.SDK_INT >= VERSION_CODES.M) {
        setTextAppearance(R.style.AlertDialogMessageTextStyle)
      } else {
        @Suppress("DEPRECATION")
        setTextAppearance(this@SubscriptionActivity, R.style.AlertDialogMessageTextStyle)
      }
    }
  }

  private fun enablePlanGroup(group: Group) {
    group.referencedIds.forEach {
      val view = findViewById<View>(it)
      view.isEnabled = true

      when (view) {
        is ViewGroup -> view.setBackgroundColor(selectedSubscriptionColor)
        is ImageView -> view.alpha = ALPHA_VIEW_ENABLED
        is TextView -> view.alpha = ALPHA_VIEW_ENABLED
      }
    }
  }

  private fun disabledPlansGroup(
    groups: Array<Group>,
    isEnabled: Boolean = false
  ) {
    groups.iterator()
      .forEach { group ->
        group.referencedIds.forEach {
          val view = findViewById<View>(it)
          view.isEnabled = isEnabled

          when (view) {
            is ViewGroup -> view.setBackgroundColor(subscriptionColor)
            is ImageView -> view.alpha = ALPHA_IMAGE_DISABLED
            is TextView -> view.alpha = ALPHA_TEXT_DISABLED
          }
        }
      }
  }
  // endregion

  // region Static
  companion object {
    // region Constants
    private const val RC_SIGN_IN = 9001
    private const val ALPHA_VIEW_ENABLED = 1.0F
    private const val ALPHA_IMAGE_DISABLED = 0.58F
    private const val ALPHA_TEXT_DISABLED = 0.72F
    // endregion

    // region Intent
    fun start(context: Context): Intent {
      return Intent(context, SubscriptionActivity::class.java)
    }
    // endregion
  }
  // endregion

}
