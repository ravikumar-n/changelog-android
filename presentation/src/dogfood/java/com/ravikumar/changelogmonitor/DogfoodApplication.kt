package com.ravikumar.changelogmonitor

import com.facebook.stetho.Stetho
import timber.log.Timber

class DogfoodApplication : ChangelogApplication() {

  override fun onCreate() {
    super.onCreate()
    Stetho.initializeWithDefaults(this)
  }

  override fun setupLogging() {
    Timber.plant(Timber.DebugTree())
  }

  override fun setupCrashlytics() {
  }
}
