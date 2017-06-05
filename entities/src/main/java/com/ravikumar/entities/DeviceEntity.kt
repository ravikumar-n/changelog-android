package com.ravikumar.entities

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.UUID

data class DeviceRequest(
  var device: DeviceInfo
)

@Entity(tableName = "Device")
data class DeviceInfo(
  @PrimaryKey
  var id: UUID = UUID.randomUUID(),
  var userId: UUID = UUID.randomUUID(),
  var uniqueId: String? = null,
  var modelInfo: String? = null,
  var platform: String? = null,
  var osVersion: String? = null,
  var token: String? = null
)
