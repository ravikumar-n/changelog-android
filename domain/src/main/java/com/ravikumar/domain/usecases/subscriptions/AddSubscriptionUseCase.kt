package com.ravikumar.domain.usecases.subscriptions

import com.ravikumar.domain.datasource.ChangelogApi
import com.ravikumar.domain.executor.ExecutionScheduler
import com.ravikumar.domain.usecases.UseCase
import com.ravikumar.entities.SubscriptionRequest
import com.ravikumar.entities.SubscriptionResponse
import io.reactivex.Single
import java.util.UUID
import javax.inject.Inject

class AddSubscriptionUseCase @Inject constructor(
  private val api: ChangelogApi,
  private val scheduler: ExecutionScheduler
) : UseCase.RxSingle<SubscriptionResponse?, AddSubscriptionUseCase.Params>() {
  override fun build(params: Params?): Single<SubscriptionResponse?> {
    requireNotNull(params)

    return api
      .postSubscription(params?.userId!!, params.request)
      .compose(scheduler.highPrioritySingle())
  }

  data class Params(
    val userId: UUID,
    val request: SubscriptionRequest
  )
}
