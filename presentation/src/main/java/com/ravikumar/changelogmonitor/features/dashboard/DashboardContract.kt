package com.ravikumar.changelogmonitor.features.dashboard

import com.ravikumar.changelogmonitor.base.MvpPresenter
import com.ravikumar.changelogmonitor.base.MvpView

interface DashboardContract {
  interface Presenter<in V : View> : MvpPresenter<V> {
    fun checkUserSignedIn()

    fun updateDeviceTokenIfRequired(firebaseToken: String)
  }

  interface View : MvpView {
    fun onUserNotSignedIn()

    fun onUserSignedIn()
  }
}
