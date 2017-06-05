package com.ravikumar.domain.usecases

import com.ravikumar.domain.datasource.ChangelogApi
import com.ravikumar.domain.executor.ExecutionScheduler
import com.ravikumar.entities.Repositories
import io.reactivex.Observable
import javax.inject.Inject

class RepositoriesUseCase @Inject constructor(
  private val api: ChangelogApi,
  private val scheduler: ExecutionScheduler
) : UseCase.RxObservable<Repositories?, RepositoriesUseCase.Params>() {

  override fun build(params: Params?): Observable<Repositories?> {
    return api
      .repositories(params?.offsetIndex ?: 0)
      .compose(scheduler.highPriorityObservable())
  }

  data class Params(val offsetIndex: Int)
}
