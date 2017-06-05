package com.ravikumar.data.local.dao

import com.ravikumar.entities.DeviceInfo
import org.junit.Test
import java.util.UUID

class DeviceDaoTest : BaseDaoTest() {

  @Test
  fun getDeviceWhenNoneExists() {
    database.deviceDao()
      .getDevice()
      .test()
      .assertNoValues()
  }

  @Test
  fun insertAndGetDevice() {
    // WHEN
    database.deviceDao()
      .saveDevice(DEVICE)

    // THEN
    database.deviceDao()
      .getDevice()
      .test()
      .assertValue { it.id == DEVICE.id && it.userId == DEVICE.userId }
  }

  @Test
  fun updateTokenReplacesExistingDevice() {
    // GIVEN
    database.deviceDao()
      .saveDevice(DEVICE)

    // WHEN
    database.deviceDao()
      .saveDevice(DEVICE.copy(token = "new"))

    // THEN
    database.deviceDao()
      .getDevice()
      .test()
      .assertValue {
        it.id == DEVICE.id && it.userId == DEVICE.userId
          && it.token == "new"
      }
  }

  @Test
  fun deleteAndGetDevice() {
    // GIVEN
    database.deviceDao()
      .saveDevice(DEVICE)

    // WHEN
    database.deviceDao()
      .deleteAllDevices()

    // THEN
    database.deviceDao()
      .getDevice()
      .test()
      .assertNoValues()
  }

  companion object {
    val DEVICE = DeviceInfo(
      id = UUID.fromString("f000aa01-0451-4000-b000-000000000000"),
      userId = UUID.fromString("f000aa01-0451-4000-b000-000000000000"),
      token = "old"
    )
  }
}
