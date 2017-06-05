package com.ravikumar.changelogmonitor.helpers

import android.os.Bundle
import android.support.annotation.Nullable
import com.google.firebase.analytics.FirebaseAnalytics

class Analytics(@Nullable val analytics: FirebaseAnalytics?) {
  companion object {
    const val PARAM_SIGN_UP_METHOD_GOOGLE = "google"
    const val PARAM_SIGN_UP_METHOD_GUEST = "guest"

    const val EVENT_THEME = "theme"
    const val THEME_DARK = "dark"
    const val THEME_LIGHT = "light"
    const val THEME_AUTO = "dayNight"
  }

  fun logSignUp(
    signUpMethod: String
  ) {
    analytics?.let {
      val bundle = Bundle()
      bundle.putString(FirebaseAnalytics.Param.SIGN_UP_METHOD, signUpMethod)
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
}
