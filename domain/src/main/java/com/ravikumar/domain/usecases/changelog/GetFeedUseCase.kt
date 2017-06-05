package com.ravikumar.domain.usecases.changelog

import com.ravikumar.domain.datasource.ChangelogApi
import com.ravikumar.domain.executor.ExecutionScheduler
import com.ravikumar.domain.usecases.UseCase
import com.ravikumar.entities.ChangelogResponse
import io.reactivex.Flowable
import java.util.UUID
import javax.inject.Inject

class GetFeedUseCase @Inject constructor(
  private val api: ChangelogApi,
  private val scheduler: ExecutionScheduler
) : UseCase.RxFlowable<ChangelogResponse?, GetFeedUseCase.Params>() {

  override fun build(params: Params?): Flowable<ChangelogResponse?> {
    return api
      .getChangelogFeed(params?.userId!!)
      .compose(scheduler.highPriorityFlowable())
  }

  data class Params(val userId: UUID?)
}
