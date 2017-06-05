package com.ravikumar.domain.usecases.subscriptions

import com.ravikumar.domain.datasource.ChangelogApi
import com.ravikumar.domain.executor.ExecutionScheduler
import com.ravikumar.domain.usecases.UseCase.RxSingle
import com.ravikumar.entities.User
import io.reactivex.Single
import javax.inject.Inject

class RemoveSubscriptionUseCase @Inject constructor(
  private val api: ChangelogApi,
  private val scheduler: ExecutionScheduler
) : RxSingle<User?, Nothing>() {

  override fun build(params: Nothing?): Single<User?> {
    return api
      .removeSubscription()
      .compose(scheduler.highPrioritySingle())
  }
}
