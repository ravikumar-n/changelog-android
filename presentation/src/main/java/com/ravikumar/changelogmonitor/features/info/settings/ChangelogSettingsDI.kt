package com.ravikumar.changelogmonitor.features.info.settings

import android.content.Context
import android.content.SharedPreferences
import com.ravikumar.changelogmonitor.R
import com.ravikumar.data.preferences.BooleanPreference
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector

@Module
class ChangelogSettingModule {

  @Provides
  fun provideAnalyticsPreference(
    sharedPreferences: SharedPreferences,
    context: Context
  ): BooleanPreference {
    return BooleanPreference(
      sharedPreferences,
      context.getString(R.string.preference_analytics),
      true
    )
  }
}

@Module
abstract class ChangelogSettingsFragmentProvider {
  @ContributesAndroidInjector(
    modules = [ChangelogSettingModule::class]
  )
  abstract fun provideDetailFragmentFactory(): ChangelogSettingsFragment
}
