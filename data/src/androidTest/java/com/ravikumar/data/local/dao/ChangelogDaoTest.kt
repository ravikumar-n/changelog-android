package com.ravikumar.data.local.dao

import org.junit.Test

class ChangelogDaoTest : BaseDaoTest() {

  @Test
  fun getChangelogReturnsEmptyListWhenNoneExists() {
    database.changelogDao()
      .getUserChangelog()
      .test()
      .assertValue(emptyList())
  }

  @Test
  fun insertAndGetChangelogs() {
    val changelogs = testHelper.getFakeChangelogs()
    database.changelogDao()
      .saveAllChangelog(*changelogs.changelogs.toTypedArray())

    database.changelogDao()
      .getUserChangelog()
      .test()
      .assertValue { it.containsAll(changelogs.changelogs) }
  }

  @Test
  fun deleteAndGetChangelog() {
    // Save all changelogs
    val changelogs = testHelper.getFakeChangelogs()
    database.changelogDao()
      .saveAllChangelog(*changelogs.changelogs.toTypedArray())

    // Delete all changelogs
    database.changelogDao()
      .deleteAllChangelogs()

    // Assert changelogs is empty after deletion
    database.changelogDao()
      .getUserChangelog()
      .test()
      .assertValue(emptyList())
  }
}
