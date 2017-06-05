package com.ravikumar.data.local.dao

import com.ravikumar.entities.User
import org.junit.Test
import java.util.UUID

class UserDaoTest : BaseDaoTest() {

  @Test
  fun getUserWhenNoneExists() {
    database.userDao()
      .getUser()
      .test()
      .assertNoValues()
  }

  @Test
  fun insertAndGetUser() {
    // WHEN
    database.userDao()
      .saveUser(USER)

    // THEN
    database.userDao()
      .getUser()
      .test()
      .assertValue { it.id == USER.id && it.name == USER.name }
  }

  @Test
  fun deleteAndGetUser() {
    // GIVEN
    database.userDao()
      .saveUser(USER)

    // WHEN
    database.userDao()
      .deleteAllUsers()

    // THEN
    database.userDao()
      .getUser()
      .test()
      .assertNoValues()
  }

  companion object {
    val USER = User(
      id = UUID.fromString("f000aa01-0451-4000-b000-000000000000"),
      name = "arya"
    )
  }
}
