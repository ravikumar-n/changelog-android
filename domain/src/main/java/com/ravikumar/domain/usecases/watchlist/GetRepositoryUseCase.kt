package com.ravikumar.domain.usecases.watchlist

import com.ravikumar.domain.datasource.ChangelogApi
import com.ravikumar.domain.executor.ExecutionScheduler
import com.ravikumar.domain.usecases.UseCase.RxMaybe
import com.ravikumar.domain.usecases.watchlist.GetRepositoryUseCase.Params
import com.ravikumar.entities.Repository
import io.reactivex.Maybe
import java.util.UUID
import javax.inject.Inject

class GetRepositoryUseCase @Inject constructor(
  private val api: ChangelogApi,
  private val scheduler: ExecutionScheduler
) : RxMaybe<Repository, Params>() {
  override fun build(params: Params?): Maybe<Repository> {
    return api
      .getRepository(params!!.uuid)
      .compose(scheduler.lowPriorityMaybe())
  }

  data class Params(val uuid: UUID)
}
