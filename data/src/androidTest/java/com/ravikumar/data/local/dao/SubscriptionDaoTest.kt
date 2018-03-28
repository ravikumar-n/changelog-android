package com.ravikumar.data.local.dao

import org.junit.Test

class SubscriptionDaoTest : BaseDaoTest() {

  @Test
  fun checkNoValuesIsReturnedWhenThereIsNoSubscriptions() {
    database.subscriptionDao()
      .getSubscription()
      .test()
      .assertNoValues()
  }

  @Test
  fun insertAndGetSubscription() {
    val fakeSubscription = testHelper.getFakeSubscription()
    database.subscriptionDao()
      .saveSubscription(fakeSubscription)

    database.subscriptionDao()
      .getSubscription()
      .test()
      .assertValue(fakeSubscription)
  }

  @Test
  fun checkSubscriptionsAreClearedAfterDeletion() {
    val fakeSubscription = testHelper.getFakeSubscription()
    database.subscriptionDao()
      .saveSubscription(fakeSubscription)

    database.subscriptionDao()
      .deleteAllSubscriptions()

    database.subscriptionDao()
      .getSubscription()
      .test()
      .assertNoValues()
  }
}
