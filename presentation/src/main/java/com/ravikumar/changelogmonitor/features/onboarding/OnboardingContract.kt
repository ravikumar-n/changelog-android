package com.ravikumar.changelogmonitor.features.onboarding

import com.ravikumar.changelogmonitor.base.MvpPresenter
import com.ravikumar.changelogmonitor.base.MvpView
import com.ravikumar.entities.DeviceInfo
import com.ravikumar.entities.User

interface OnboardingContract {
  interface Presenter<in V : View> : MvpPresenter<V> {
    fun createGuestUser()

    fun createUser(
      idToken: String,
      userName: String,
      email: String
    )

    fun registerDevice(deviceInfo: DeviceInfo)
  }

  interface View : MvpView {
    fun onGuestUserCreated(user: User)

    fun onUserCreated(user: User)

    fun onGoogleSignIn(userName: String)

    fun onDeviceRegistered(deviceInfo: DeviceInfo)
  }
}
