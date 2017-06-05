package com.ravikumar.domain.usecases.user

import com.ravikumar.domain.datasource.ChangelogApi
import com.ravikumar.domain.executor.ExecutionScheduler
import com.ravikumar.domain.usecases.UseCase
import com.ravikumar.entities.NewUser
import com.ravikumar.entities.User
import com.ravikumar.entities.UserResponse
import io.reactivex.Single
import javax.inject.Inject

class ConnectUserUseCase @Inject constructor(
  private val api: ChangelogApi,
  private val scheduler: ExecutionScheduler
) : UseCase.RxSingle<UserResponse?, ConnectUserUseCase.Params>() {

  override fun build(params: Params?): Single<UserResponse?> {
    requireNotNull(params)
    return api
      .connectUser(NewUser(token = params!!.idToken, user = params.user))
      .compose(scheduler.highPrioritySingle())
  }

  data class Params(
    val idToken: String,
    val user: User
  )
}
