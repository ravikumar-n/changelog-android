package com.ravikumar.data.local.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.ravikumar.entities.User
import io.reactivex.Single

@Dao
interface UserDao {

  @Query("SELECT * FROM User LIMIT 1")
  fun getUser(): Single<User?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun saveUser(user: User)

  @Query("DELETE FROM User")
  fun deleteAllUsers()
}
