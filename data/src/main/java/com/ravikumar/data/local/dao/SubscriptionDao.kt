package com.ravikumar.data.local.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.ravikumar.entities.ChangelogSubscription
import io.reactivex.Maybe

@Dao
interface SubscriptionDao {

  @Query("SELECT * FROM Subscription LIMIT 1")
  fun getSubscription(): Maybe<ChangelogSubscription>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun saveSubscription(changelogSubscription: ChangelogSubscription)

  @Query("DELETE FROM Subscription")
  fun deleteAllSubscriptions()
}
