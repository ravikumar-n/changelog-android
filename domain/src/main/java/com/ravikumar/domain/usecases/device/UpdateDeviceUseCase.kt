package com.ravikumar.domain.usecases.device

import com.ravikumar.domain.datasource.ChangelogApi
import com.ravikumar.domain.executor.ExecutionScheduler
import com.ravikumar.domain.usecases.UseCase.RxMaybe
import com.ravikumar.domain.usecases.device.UpdateDeviceUseCase.Params
import com.ravikumar.entities.DeviceInfo
import io.reactivex.Maybe
import javax.inject.Inject

class UpdateDeviceUseCase @Inject constructor(
  private val api: ChangelogApi,
  private val scheduler: ExecutionScheduler
) : RxMaybe<DeviceInfo?, Params>() {
  override fun build(params: Params?): Maybe<DeviceInfo?> {
    require(params?.firebaseToken != null) {
      "Firebase token can't be null"
    }

    return api
      .updateDeviceToken(params!!.firebaseToken)
      .compose(scheduler.lowPriorityMaybe())
  }

  data class Params(val firebaseToken: String)
}
