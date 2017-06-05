package com.ravikumar.data

import com.nhaarman.mockito_kotlin.verify
import com.ravikumar.data.local.LocalStore
import com.ravikumar.data.remote.RemoteStore
import com.ravikumar.domain.executor.ThreadScheduler
import com.ravikumar.shared.TestHelper
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ChangelogDataManagerTest {
  @Mock private lateinit var localStore: LocalStore
  @Mock private lateinit var remoteStore: RemoteStore
  @Mock private lateinit var scheduler: ThreadScheduler

  private lateinit var dataManager: ChangelogDataManager
  private lateinit var testHelper: TestHelper

  @Before
  fun setUp() {
    MockitoAnnotations.initMocks(this)
    testHelper = TestHelper()
    dataManager = ChangelogDataManager(localStore, remoteStore, scheduler)
  }

  @Test
  fun `Verify get user loads from local database`() {
    dataManager.getUser()

    verify(localStore).getUser()
  }
}
