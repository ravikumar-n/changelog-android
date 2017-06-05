package com.ravikumar.changelogmonitor

import android.support.annotation.Nullable
import android.util.Log
import com.crashlytics.android.Crashlytics
import timber.log.Timber

class CrashlyticsTree : Timber.Tree() {

  // region Static
  companion object {
    private const val CRASHLYTICS_KEY_PRIORITY = "priority"
    private const val CRASHLYTICS_KEY_TAG = "tag"
    private const val CRASHLYTICS_KEY_MESSAGE = "message"
  }
  // endregion

  // region Timber
  override fun log(
    priority: Int, @Nullable tag: String?, @Nullable message: String,
    @Nullable t: Throwable?
  ) {
    if (priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO) {
      return
    }

    Crashlytics.setInt(CRASHLYTICS_KEY_PRIORITY, priority)
    Crashlytics.setString(CRASHLYTICS_KEY_TAG, tag)
    Crashlytics.setString(CRASHLYTICS_KEY_MESSAGE, message)

    if (t == null) {
      Crashlytics.logException(Exception(message))
    } else {
      Crashlytics.logException(t)
    }
  }
  // endregion
}
