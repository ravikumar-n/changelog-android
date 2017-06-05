package com.ravikumar.changelogmonitor.features.info

import com.ravikumar.changelogmonitor.base.MvpPresenter
import com.ravikumar.changelogmonitor.base.MvpView
import com.ravikumar.entities.ChangelogSubscription
import com.ravikumar.entities.User
import java.util.UUID

interface InfoContract {
  interface Presenter<in V : View> : MvpPresenter<V> {
    fun fetchUserInfo()

    fun fetchSubscriptionInfo(userId: UUID)

    fun signout()
  }

  interface View : MvpView {
    fun showUserInfo(user: User)

    fun showSubscription(subscription: ChangelogSubscription)

    fun showFreePlan()

    fun onUserSignedOut()
  }
}
