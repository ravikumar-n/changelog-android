package com.ravikumar.changelogmonitor

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.ravikumar.changelogmonitor.R.string
import com.ravikumar.changelogmonitor.di.application.DaggerApplicationComponent
import com.ravikumar.changelogmonitor.helpers.Analytics
import com.ravikumar.changelogmonitor.helpers.events.RxBus
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import timber.log.Timber
import javax.inject.Inject

@SuppressLint("Registered")
open class ChangelogApplication : DaggerApplication(), OnSharedPreferenceChangeListener {

  // region DI
  @Inject lateinit var gsonInstance: Gson
  @Inject lateinit var preferences: SharedPreferences
  // endregion

  // region Lifecycle
  override fun onCreate() {
    setupLeakDetection()
    super.onCreate()
    setupCrashlytics()
    setupLogging()
    init()
    setupAnalytics()
  }
  // endregion

  // region Overrides
  override fun onSharedPreferenceChanged(
    sharedPreferences: SharedPreferences?,
    key: String?
  ) {
    if (key?.equals(getString(R.string.preference_analytics)) == true) {
      setupAnalytics()
    }
  }
  // endregion

  // region Protected
  protected open fun setupLeakDetection() = Unit

  protected open fun setupLogging() = Unit

  protected open fun setupCrashlytics() = Unit
  // endregion

  // region Overrides
  override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
    return DaggerApplicationComponent
      .builder()
      .application(this)
      .build()
  }
  // endregion

  // region Private
  private fun init() {
    gson = gsonInstance
    preferences.registerOnSharedPreferenceChangeListener(this)
  }

  private fun setupAnalytics() {
    analytics = if (preferences.getBoolean(getString(string.preference_analytics), false)) {
      Timber.d("Analytics is enabled")
      Analytics(FirebaseAnalytics.getInstance(this))
    } else {
      Timber.d("Analytics is disabled")
      null
    }
  }
  // endregion

  // region Static
  companion object {
    // region Variables
    var gson: Gson? = null
    var analytics: Analytics? = null
    val rxBus: RxBus = RxBus()
    // endregion

    operator fun get(context: Context): ChangelogApplication {
      return context.applicationContext as ChangelogApplication
    }
  }
  // endregion
}
