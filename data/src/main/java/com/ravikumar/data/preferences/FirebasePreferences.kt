package com.ravikumar.data.preferences

import android.content.SharedPreferences
import javax.inject.Inject

class FirebasePreferences @Inject constructor(
  private val preferences: SharedPreferences
) {
  // region Firebase token
  fun getFirebaseToken(): String? = preferences.getString(
    PREFS_FIRE_BASE_TOKEN, null
  )

  fun setToken(token: String) {
    requireNotNull(token)
    val editor = preferences.edit()
    with(editor) {
      putString(PREFS_FIRE_BASE_TOKEN, token)
      apply()
    }
  }
  // endregion

  // region Clear preferences
  fun clear() {
    if (preferences.contains(
        PREFS_FIRE_BASE_TOKEN
      )
    ) {
      preferences.edit()
        .remove(
          PREFS_FIRE_BASE_TOKEN
        )
        .apply()
    }
  }
  // endregion

  // region Static
  companion object {
    // region Constants
    const val PREFS_FIRE_BASE_TOKEN = "firebase_token"
    // endregion
  }
  // endregion
}
