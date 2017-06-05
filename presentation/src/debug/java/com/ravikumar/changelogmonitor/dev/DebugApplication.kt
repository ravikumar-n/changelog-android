package com.ravikumar.changelogmonitor.dev

import android.os.StrictMode
import com.facebook.stetho.Stetho
import com.ravikumar.changelogmonitor.ChangelogApplication
import com.squareup.leakcanary.LeakCanary
import timber.log.Timber

class DebugApplication : ChangelogApplication() {
  override fun setupLeakDetection() {
    Stetho.initializeWithDefaults(this)

    StrictMode.setThreadPolicy(
      StrictMode.ThreadPolicy.Builder()
        .detectAll()
        .penaltyLog()
        .build()
    )
    StrictMode.setVmPolicy(
      StrictMode.VmPolicy.Builder()
        .detectAll()
        .penaltyLog()
        .build()
    )

    LeakCanary.install(this)
  }

  override fun setupLogging() {
    Timber.plant(Timber.DebugTree())
  }
}
