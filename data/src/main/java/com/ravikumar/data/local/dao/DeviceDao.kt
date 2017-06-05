package com.ravikumar.data.local.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.ravikumar.entities.DeviceInfo
import io.reactivex.Single

@Dao
interface DeviceDao {

  @Query("SELECT * FROM Device LIMIT 1")
  fun getDevice(): Single<DeviceInfo?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun saveDevice(deviceInfo: DeviceInfo)

  @Query("DELETE FROM Device")
  fun deleteAllDevices()
}
