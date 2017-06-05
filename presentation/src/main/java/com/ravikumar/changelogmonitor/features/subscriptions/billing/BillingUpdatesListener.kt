package com.ravikumar.changelogmonitor.features.subscriptions.billing

import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails

/**
 * Listener to the updates that happen when purchases list was
 * updated
 */
interface BillingUpdatesListener {
  fun onBillingClientSetupFinished()

  fun onPurchasesUpdated(purchases: List<Purchase>)

  fun onSkuDetailsRetrieved(
    responseCode: Int,
    skuDetailsList: MutableList<SkuDetails>?
  )
}
