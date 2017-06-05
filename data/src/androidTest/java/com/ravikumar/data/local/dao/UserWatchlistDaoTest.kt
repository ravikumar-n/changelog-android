package com.ravikumar.data.local.dao

import org.junit.Test
import java.util.UUID

class UserWatchlistDaoTest : BaseDaoTest() {

  @Test
  fun userWatchlistReturnsEmptyListWhenNoneExists() {
    database.userWatchlistDao()
      .getAllUserWatchlist()
      .test()
      .assertValue(emptyList())
  }

  @Test
  fun insertAndGetUserWatchlist() {
    val saveWatchlist = testHelper.getFakeWatchList()
    database.userWatchlistDao()
      .saveAllUserWatchlist(*saveWatchlist.items.toTypedArray())

    database.userWatchlistDao()
      .getAllUserWatchlist()
      .test()
      .assertValue { it.containsAll(saveWatchlist.items) }
  }

  @Test
  fun retrieveSelectedWatchlistItems() {
    // Save all the watchlist items
    val fakeWatchlist = testHelper.getFakeWatchList()
    database.userWatchlistDao()
      .saveAllUserWatchlist(*fakeWatchlist.items.toTypedArray())

    // Retrieve only the selected first two items
    val ids: List<UUID> = fakeWatchlist.items.take(2)
      .map { it.id }
    database.userWatchlistDao()
      .getUserWatchlist(ids)
      .test()
      // Assert only the first two items are retrieved
      .assertValue { it.containsAll(fakeWatchlist.items.take(2)) }
  }

  @Test
  fun deleteAndGetUserWatchlist() {
    // Save single item to the user watchlist
    database.userWatchlistDao()
      .saveUserWatchlist(
        testHelper.getFakeWatchList().items.take(1)[0]
      )

    // Delete the watchlist
    database.userWatchlistDao()
      .deleteAllWatchlist()

    // Assert user watchlist is empty after deletion
    database.userWatchlistDao()
      .getAllUserWatchlist()
      .test()
      .assertValue(emptyList())
  }
}
