package com.ravikumar.domain.usecases.device

import com.ravikumar.domain.datasource.ChangelogApi
import com.ravikumar.domain.executor.ExecutionScheduler
import com.ravikumar.domain.usecases.UseCase.RxSingle
import com.ravikumar.domain.usecases.device.RegisterDeviceUseCase.Params
import com.ravikumar.entities.DeviceInfo
import io.reactivex.Single
import javax.inject.Inject

class RegisterDeviceUseCase @Inject constructor(
  private val api: ChangelogApi,
  private val scheduler: ExecutionScheduler
) : RxSingle<DeviceInfo?, Params>() {

  override fun build(params: Params?): Single<DeviceInfo?> {
    require(params?.deviceInfo != null) {
      "Device info can't be null"
    }

    return api
      .registerDevice(params!!.deviceInfo)
      .compose(scheduler.highPrioritySingle())
  }

  data class Params(val deviceInfo: DeviceInfo)
}
