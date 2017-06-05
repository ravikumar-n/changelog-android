package com.ravikumar.data.local.dao

import org.junit.Test

class ChangelogDetailDaoTest : BaseDaoTest() {

  @Test
  fun getChangelogDetailReturnsNoValuesWhenNoneExists() {
    database.changelogDetailDao()
      .getChangelogDetail(testHelper.getFakeWatchListIds().items[0])
      .test()
      .assertNoValues()
  }

  @Test
  fun insertAndGetChangelogDetail() {
    val repoId = testHelper.getFakeWatchListIds().items[0]
    val changelogDetail = testHelper.getFakeChangelogDetail()
    database.changelogDetailDao()
      .saveChangelogDetail(changelogDetail)

    database.changelogDetailDao()
      .getChangelogDetail(repoId)
      .test()
      .assertValue { it.repoId == repoId }
  }
}
