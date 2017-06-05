package com.ravikumar.entities

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.Date
import java.util.UUID

data class NewUser(
  val token: String?,
  val user: User
)

data class UserResponse(
  val user: User
)

@Entity(tableName = "User")
data class User(
  @PrimaryKey
  var id: UUID = UUID.randomUUID(),
  var subscriptionId: UUID? = null,
  var emailId: String? = null,
  var name: String? = null,
  var picture: String? = null,
  var givenName: String? = null,
  var locale: String? = "en",
  var createdAt: Date? = null,
  var updatedAt: Date? = null
)
