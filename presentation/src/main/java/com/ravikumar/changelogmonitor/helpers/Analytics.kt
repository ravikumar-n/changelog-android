package com.ravikumar.changelogmonitor.helpers

import android.os.Bundle
import android.support.annotation.Nullable
import com.google.firebase.analytics.FirebaseAnalytics

class Analytics(@Nullable val analytics: FirebaseAnalytics?) {
  companion object {
    // region Sign Up
    const val EVENT_SIGN_UP = "signup_method"
    const val PARAM_SIGN_UP_METHOD_GOOGLE = "google"
    const val PARAM_SIGN_UP_METHOD_GUEST = "guest"
    // endregion

    // region Theme
    const val EVENT_THEME = "theme"
    const val THEME_DARK = "dark"
    const val THEME_LIGHT = "light"
    const val THEME_AUTO = "dayNight"
    // endregion
  }

  fun logSignUp(
    signUpMethod: String
  ) {
    analytics?.let {
      val bundle = Bundle()
      bundle.putString(EVENT_SIGN_UP, signUpMethod)
      it.logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle)
    }
  }

  fun logSelectedTheme(themeType: String) {
    analytics?.let {
      val bundle = Bundle()
      bundle.putString(EVENT_THEME, themeType)

      it.logEvent(
        EVENT_THEME,
        bundle
      )
    }
  }

  fun logShareEvent() {
    analytics?.let {
      val bundle = Bundle()
      bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Text")
      bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Changelog Monitor-Android")

      it.logEvent(
        FirebaseAnalytics.Event.SHARE,
        bundle
      )
    }
  }

  fun logNotificationViewed(name: String) {
    analytics?.let {
      val bundle = Bundle()
      bundle.putString(FirebaseAnalytics.Param.CONTENT, name)
      it.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle)
    }
  }
}
