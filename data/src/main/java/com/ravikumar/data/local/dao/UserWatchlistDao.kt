package com.ravikumar.data.local.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.ravikumar.entities.Repository
import io.reactivex.Maybe
import io.reactivex.Single
import java.util.UUID

@Dao
interface UserWatchlistDao {

  @Query("SELECT * FROM Watchlist")
  fun getAllUserWatchlist(): Single<List<Repository>>

  @Query("SELECT * from Watchlist WHERE id = (:uuid)")
  fun getRepository(uuid: UUID): Maybe<Repository>

  @Query("SELECT * from Watchlist WHERE id IN (:repoIds)")
  fun getUserWatchlist(repoIds: List<UUID>): Single<List<Repository>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun saveUserWatchlist(repository: Repository)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun saveAllUserWatchlist(vararg repositories: Repository)

  @Query("DELETE FROM Watchlist")
  fun deleteAllWatchlist()
}
