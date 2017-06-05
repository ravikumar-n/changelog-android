package com.ravikumar.domain.usecases.signin

import com.ravikumar.domain.datasource.ChangelogApi
import com.ravikumar.domain.executor.ExecutionScheduler
import com.ravikumar.domain.usecases.UseCase
import com.ravikumar.entities.NewUser
import com.ravikumar.entities.User
import com.ravikumar.entities.UserResponse
import io.reactivex.Single
import javax.inject.Inject

class GoogleSignInUseCase @Inject constructor(
  private val api: ChangelogApi,
  private val scheduler: ExecutionScheduler
) : UseCase.RxSingle<UserResponse?, GoogleSignInUseCase.Params>() {

  override fun build(params: Params?): Single<UserResponse?> {
    requireNotNull(params)
    return api
      .createUser(NewUser(token = params!!.idToken, user = User(emailId = params.email)))
      .compose(scheduler.highPrioritySingle())
  }

  data class Params(
    val idToken: String,
    val email: String
  )
}
