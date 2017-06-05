package com.ravikumar.entities.converters

import android.arch.persistence.room.TypeConverter
import java.util.UUID

class UUIDConverter {
  @TypeConverter
  fun toUuid(uuid: String?): UUID? {
    return if (uuid == null) null else UUID.fromString(uuid)
  }

  @TypeConverter
  fun toString(uuid: UUID?): String? = uuid?.toString()
}
