package com.ravikumar.changelogmonitor.features.notification

import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import com.ravikumar.data.preferences.FirebasePreferences
import dagger.android.AndroidInjection
import javax.inject.Inject

class ChangelogFirebaseInstanceIdService : FirebaseInstanceIdService() {

  @Inject lateinit var fireBasePreference: FirebasePreferences

  override fun onTokenRefresh() {
    AndroidInjection.inject(this)
    fireBasePreference.apply {
      val refreshedToken: String? = FirebaseInstanceId.getInstance()
        .token
      refreshedToken?.let { setToken(refreshedToken) }
    }
  }
}
