package com.ravikumar.domain.usecases.user

import com.ravikumar.domain.datasource.ChangelogApi
import com.ravikumar.domain.executor.ExecutionScheduler
import com.ravikumar.domain.usecases.UseCase.None
import com.ravikumar.domain.usecases.UseCase.RxSingle
import com.ravikumar.entities.User
import io.reactivex.Single
import javax.inject.Inject

class GetUserUseCase @Inject constructor(
  private val api: ChangelogApi,
  private val scheduler: ExecutionScheduler
) : RxSingle<User?, None>() {

  override fun build(params: None?): Single<User?> {
    return api.getUser()
      .compose(scheduler.highPrioritySingle())
  }
}
