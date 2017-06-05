package com.ravikumar.entities

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.Date
import java.util.UUID

data class ChangelogResponse(
  val changelogs: List<Changelog>,
  val repositories: List<Repository>
)

@Entity(tableName = "ChangelogDetail")
data class ChangelogDetail(
  @PrimaryKey
  var repoId: UUID = UUID.randomUUID(),
  var changelog: String = ""
)

@Entity(tableName = "Changelog")
data class Changelog(
  @PrimaryKey
  var repoId: UUID = UUID.randomUUID(),
  var rawContent: String = "",
  var latestVersion: String = "",
  var latestChangelog: String = "",
  var sha: String = "",
  var createdAt: Date = Date(),
  var updatedAt: Date = Date()
)
