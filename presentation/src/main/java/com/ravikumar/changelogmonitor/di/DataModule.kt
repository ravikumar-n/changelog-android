package com.ravikumar.changelogmonitor.di

import android.arch.persistence.room.Room
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.ravikumar.changelogmonitor.di.application.ApplicationModule
import com.ravikumar.data.local.ChangelogDatabase
import com.ravikumar.data.local.LocalStore
import com.ravikumar.data.local.LocalStoreContract
import com.ravikumar.data.local.dao.ChangelogDao
import com.ravikumar.data.local.dao.ChangelogDetailDao
import com.ravikumar.data.local.dao.DeviceDao
import com.ravikumar.data.local.dao.SubscriptionDao
import com.ravikumar.data.local.dao.UserDao
import com.ravikumar.data.local.dao.UserWatchlistDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [ApplicationModule::class])
class DataModule {
  @Provides @Singleton
  fun provideSharedPreferences(context: Context): SharedPreferences {
    return PreferenceManager.getDefaultSharedPreferences(context)
  }

  @Singleton
  @Provides
  fun providesDatabase(context: Context): ChangelogDatabase {
    return Room.databaseBuilder(context, ChangelogDatabase::class.java, "changelog")
      .build()
  }

  @Singleton
  @Provides
  fun providesUserDao(db: ChangelogDatabase): UserDao {
    return db.userDao()
  }

  @Singleton
  @Provides
  fun providesDeviceDao(db: ChangelogDatabase): DeviceDao {
    return db.deviceDao()
  }

  @Singleton
  @Provides
  fun providesUserWatchlistDao(db: ChangelogDatabase): UserWatchlistDao {
    return db.userWatchlistDao()
  }

  @Singleton
  @Provides
  fun provideSubscriptionDao(db: ChangelogDatabase): SubscriptionDao {
    return db.subscriptionDao()
  }

  @Singleton
  @Provides
  fun providesChangelogDao(db: ChangelogDatabase): ChangelogDao {
    return db.changelogDao()
  }

  @Singleton
  @Provides
  fun providesChangelogDetailDao(db: ChangelogDatabase): ChangelogDetailDao {
    return db.changelogDetailDao()
  }

  @Singleton
  @Provides
  fun userWatchlist(
    userDao: UserDao,
    deviceDao: DeviceDao,
    userWatchlistDao: UserWatchlistDao,
    subscriptionDao: SubscriptionDao,
    changelogDao: ChangelogDao,
    changelogDetailDao: ChangelogDetailDao
  ): LocalStoreContract {
    return LocalStore(
      userDao,
      deviceDao,
      userWatchlistDao,
      subscriptionDao,
      changelogDao,
      changelogDetailDao
    )
  }
}
