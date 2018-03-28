package com.ravikumar.domain.usecases.watchlist

import com.ravikumar.domain.datasource.ChangelogApi
import com.ravikumar.domain.executor.ExecutionScheduler
import com.ravikumar.domain.usecases.UseCase
import com.ravikumar.domain.usecases.UseCase.None
import com.ravikumar.entities.Repository
import io.reactivex.Flowable
import javax.inject.Inject

class GetWatchListUseCase @Inject constructor(
  private val api: ChangelogApi,
  private val scheduler: ExecutionScheduler
) : UseCase.RxFlowable<List<Repository>?, None>() {

  override fun build(params: None?): Flowable<List<Repository>?> {
    return api
      .getWatchlist()
      .compose(scheduler.highPriorityFlowable())
  }
}
