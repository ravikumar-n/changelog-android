package com.ravikumar.data.local.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.ravikumar.entities.ChangelogDetail
import io.reactivex.Single
import java.util.UUID

@Dao
interface ChangelogDetailDao {
  @Query("SELECT * FROM ChangelogDetail WHERE repoId = (:repoId)")
  fun getChangelogDetail(repoId: UUID): Single<ChangelogDetail?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun saveChangelogDetail(detail: ChangelogDetail)

  @Query("DELETE FROM ChangelogDetail")
  fun deleteAllDetails()
}
