package com.ravikumar.changelogmonitor.features.subscriptions

import com.android.billingclient.api.Purchase
import com.ravikumar.changelogmonitor.base.MvpPresenter
import com.ravikumar.changelogmonitor.base.MvpView
import com.ravikumar.entities.ChangelogSubscription
import com.ravikumar.entities.SubscriptionResponse
import com.ravikumar.entities.User

interface SubscriptionContract {
  interface Presenter<in V : View> : MvpPresenter<V> {
    fun checkAndRegisterPurchase(purchase: Purchase)

    fun removeSubscriptionsIfAny()

    fun isGuestUser(): Boolean

    fun connectUser(
      idToken: String,
      userName: String,
      email: String
    )
  }

  interface View : MvpView {
    fun onSubscriptionPurchased(subscriptionResponse: SubscriptionResponse)

    fun onSubscriptionRemoved()

    fun showSubscription(subscription: ChangelogSubscription)

    fun showFreePlan()

    fun onFailedToRegisterSubscription(error: Pair<Int, String?>)

    fun onGoogleSignIn(userName: String)

    fun onGoogleSignInSuccess(user: User)

    fun onGoogleSignInFailed()
  }
}
