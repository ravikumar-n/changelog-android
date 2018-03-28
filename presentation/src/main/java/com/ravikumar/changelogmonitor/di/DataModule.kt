package com.ravikumar.changelogmonitor.di

import android.arch.persistence.room.Room
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.ravikumar.changelogmonitor.DATABASE_NAME
import com.ravikumar.changelogmonitor.di.application.ApplicationModule
import com.ravikumar.data.local.ChangelogDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [ApplicationModule::class])
class DataModule {
  @Provides @Singleton
  fun provideSharedPreferences(context: Context): SharedPreferences =
    PreferenceManager.getDefaultSharedPreferences(context)

  @Singleton
  @Provides
  fun providesDatabase(context: Context) =
    Room.databaseBuilder(context, ChangelogDatabase::class.java, DATABASE_NAME)
      .build()

  @Singleton
  @Provides
  fun providesUserDao(db: ChangelogDatabase) = db.userDao()

  @Singleton
  @Provides
  fun providesDeviceDao(db: ChangelogDatabase) = db.deviceDao()

  @Singleton
  @Provides
  fun providesUserWatchlistDao(db: ChangelogDatabase) = db.userWatchlistDao()

  @Singleton
  @Provides
  fun provideSubscriptionDao(db: ChangelogDatabase) = db.subscriptionDao()

  @Singleton
  @Provides
  fun providesChangelogDao(db: ChangelogDatabase) = db.changelogDao()

  @Singleton
  @Provides
  fun providesChangelogDetailDao(db: ChangelogDatabase) = db.changelogDetailDao()
}
