package com.ravikumar.changelogmonitor.di.application

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.ravikumar.changelogmonitor.R.integer
import com.ravikumar.changelogmonitor.R.string
import com.ravikumar.changelogmonitor.framework.extensions.getInt
import com.ravikumar.changelogmonitor.helpers.SchedulerThreads
import com.ravikumar.data.preferences.IntPreference
import com.ravikumar.domain.executor.ExecutionScheduler
import com.ravikumar.domain.executor.SchedulerProvider
import com.ravikumar.domain.executor.ThreadScheduler
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object ApplicationModule {

  @Singleton @Provides @JvmStatic
  fun provideContext(application: Application): Context = application

  @Singleton @Provides @JvmStatic
  fun provideSchedulerThreads(): SchedulerProvider = SchedulerThreads()

  @Singleton @Provides @JvmStatic
  fun provideThreadScheduler(provider: SchedulerProvider): ExecutionScheduler =
    ThreadScheduler(provider)

  @Provides @Singleton @JvmStatic
  fun provideThemePreference(
    sharedPreferences: SharedPreferences,
    context: Context
  ): IntPreference {
    return IntPreference(
      sharedPreferences,
      context.getString(string.preference_theme),
      context.getInt(integer.theme_light)
    )
  }
}
