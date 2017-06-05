package com.ravikumar.data.local.dao

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.ravikumar.data.local.ChangelogDatabase
import com.ravikumar.shared.TestHelper
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
abstract class BaseDaoTest {

  @get:Rule
  var rule = InstantTaskExecutorRule()

  lateinit var database: ChangelogDatabase
  lateinit var testHelper: TestHelper

  @Before
  fun initDb() {
    val context = InstrumentationRegistry.getContext()
    database = Room.inMemoryDatabaseBuilder(
      context,
      ChangelogDatabase::class.java
    )
      .allowMainThreadQueries()
      .build()

    testHelper = TestHelper(context)
  }

  @After
  fun closeDb() {
    database.close()
  }
}
