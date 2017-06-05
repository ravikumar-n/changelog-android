package com.ravikumar.domain.usecases.changelog

import com.ravikumar.domain.datasource.ChangelogApi
import com.ravikumar.domain.executor.ExecutionScheduler
import com.ravikumar.domain.usecases.UseCase
import com.ravikumar.entities.ChangelogDetail
import io.reactivex.Flowable
import java.util.UUID
import javax.inject.Inject

class GetChangelogDetailUseCase @Inject constructor(
  private val api: ChangelogApi,
  private val scheduler: ExecutionScheduler
) : UseCase.RxFlowable<ChangelogDetail, GetChangelogDetailUseCase.Params>() {

  override fun build(params: Params?): Flowable<ChangelogDetail> {
    if (params?.fetchShortChangelog == true) {
      return api.getShortChangelog(params.repoId)
        .toFlowable()
        .compose(scheduler.highPriorityFlowable())
    }

    return api
      .getChangelogDetail(params?.repoId!!)
      .compose(scheduler.highPriorityFlowable())
  }

  data class Params(
    val repoId: UUID,
    val fetchShortChangelog: Boolean = false
  )
}
