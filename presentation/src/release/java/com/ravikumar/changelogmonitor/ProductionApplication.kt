package com.ravikumar.changelogmonitor

import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import timber.log.Timber

class ProductionApplication : ChangelogApplication() {
  override fun setupLogging() {
    // TODO Do we need this? Timber.plant(CrashlyticsTree())
  }

  override fun setupCrashlytics() {
    val fabric = Fabric.Builder(this)
      .kits(Crashlytics())
      .build()
    Fabric.with(fabric)
  }
}
