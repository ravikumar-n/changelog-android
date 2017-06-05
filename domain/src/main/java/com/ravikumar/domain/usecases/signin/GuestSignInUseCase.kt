package com.ravikumar.domain.usecases.signin

import com.ravikumar.domain.datasource.ChangelogApi
import com.ravikumar.domain.executor.ExecutionScheduler
import com.ravikumar.domain.usecases.UseCase
import com.ravikumar.domain.usecases.UseCase.None
import com.ravikumar.entities.NewUser
import com.ravikumar.entities.User
import com.ravikumar.entities.UserResponse
import io.reactivex.Single
import javax.inject.Inject

class GuestSignInUseCase @Inject constructor(
  private val api: ChangelogApi,
  private val scheduler: ExecutionScheduler
) : UseCase.RxSingle<UserResponse?, None>() {

  override fun build(params: None?): Single<UserResponse?> {
    return api
      .createGuestUser(NewUser(null, User()))
      .compose(scheduler.highPrioritySingle())
  }
}
