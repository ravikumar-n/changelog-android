package com.ravikumar.entities

import java.util.UUID

data class AddWatchlist(
  val items: List<UUID>
)

data class Watchlist(
  val items: List<Repository> = emptyList()
)
