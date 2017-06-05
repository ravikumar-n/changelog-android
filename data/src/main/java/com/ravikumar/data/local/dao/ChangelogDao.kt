package com.ravikumar.data.local.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.ravikumar.entities.Changelog
import io.reactivex.Maybe
import io.reactivex.Single
import java.util.UUID

@Dao
interface ChangelogDao {
  @Query("SELECT * FROM Changelog")
  fun getUserChangelog(): Single<List<Changelog>>

  @Query("SELECT * FROM Changelog WHERE repoId = (:repoId)")
  fun getChangelog(repoId: UUID): Maybe<Changelog>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun saveAllChangelog(vararg changelog: Changelog)

  @Query("DELETE FROM Changelog")
  fun deleteAllChangelogs()
}
