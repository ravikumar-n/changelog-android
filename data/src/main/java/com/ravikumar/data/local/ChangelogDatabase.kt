package com.ravikumar.data.local

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.ravikumar.data.local.dao.ChangelogDao
import com.ravikumar.data.local.dao.ChangelogDetailDao
import com.ravikumar.data.local.dao.DeviceDao
import com.ravikumar.data.local.dao.SubscriptionDao
import com.ravikumar.data.local.dao.UserDao
import com.ravikumar.data.local.dao.UserWatchlistDao
import com.ravikumar.entities.Changelog
import com.ravikumar.entities.ChangelogDetail
import com.ravikumar.entities.ChangelogSubscription
import com.ravikumar.entities.DeviceInfo
import com.ravikumar.entities.Repository
import com.ravikumar.entities.User
import com.ravikumar.entities.converters.DateConverter
import com.ravikumar.entities.converters.UUIDConverter

@Database(
  entities = [
    User::class, DeviceInfo::class, Repository::class,
    ChangelogSubscription::class, Changelog::class, ChangelogDetail::class
  ],
  version = 1
)
@TypeConverters(UUIDConverter::class, DateConverter::class)
abstract class ChangelogDatabase : RoomDatabase() {

  abstract fun userDao(): UserDao

  abstract fun deviceDao(): DeviceDao

  abstract fun userWatchlistDao(): UserWatchlistDao

  abstract fun subscriptionDao(): SubscriptionDao

  abstract fun changelogDao(): ChangelogDao

  abstract fun changelogDetailDao(): ChangelogDetailDao
}
