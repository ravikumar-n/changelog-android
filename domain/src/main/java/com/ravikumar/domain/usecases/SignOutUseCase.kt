package com.ravikumar.domain.usecases

import com.ravikumar.domain.datasource.ChangelogApi
import com.ravikumar.domain.executor.ExecutionScheduler
import com.ravikumar.domain.usecases.UseCase.None
import io.reactivex.Completable
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
  private val api: ChangelogApi,
  private val scheduler: ExecutionScheduler
) : UseCase.RxCompletable<None>() {
  override fun build(params: None?): Completable {
    return api
      .signout()
      .compose(scheduler.highPriorityCompletable())
  }
}
