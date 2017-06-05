package com.ravikumar.domain.usecases.subscriptions

import com.ravikumar.domain.datasource.ChangelogApi
import com.ravikumar.domain.executor.ExecutionScheduler
import com.ravikumar.domain.usecases.UseCase
import com.ravikumar.domain.usecases.subscriptions.GetSubscriptionUseCase.Params
import com.ravikumar.entities.ChangelogSubscription
import io.reactivex.Single
import java.util.UUID
import javax.inject.Inject

class GetSubscriptionUseCase @Inject constructor(
  private val api: ChangelogApi,
  private val scheduler: ExecutionScheduler
) : UseCase.RxSingle<ChangelogSubscription?, Params>() {

  override fun build(params: Params?): Single<ChangelogSubscription?> {
    return api
      .getSubscription(userId = params?.userId!!)
      .compose(scheduler.highPrioritySingle())
  }

  data class Params(val userId: UUID?)
}
