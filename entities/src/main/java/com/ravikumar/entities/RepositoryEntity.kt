package com.ravikumar.entities

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.UUID

data class Repositories(
  var repositories: List<Repository>,
  var meta: Meta
)

@Entity(tableName = "Watchlist")
data class Repository(
  @PrimaryKey
  var id: UUID = UUID.randomUUID(),
  var name: String = "",
  var description: String = "",
  var url: String = "",
  var owner: String = "",
  var tags: String = ""
)

data class Meta(
  val totalPages: Int,
  val currentPage: Int,
  val offsetIndex: Int,
  val itemsPerPage: Int
)
