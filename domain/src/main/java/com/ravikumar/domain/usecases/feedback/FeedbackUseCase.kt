package com.ravikumar.domain.usecases.feedback

import com.ravikumar.domain.datasource.ChangelogApi
import com.ravikumar.domain.executor.ExecutionScheduler
import com.ravikumar.domain.usecases.UseCase
import com.ravikumar.entities.Feedback
import io.reactivex.Single
import javax.inject.Inject

class FeedbackUseCase @Inject constructor(
  private val api: ChangelogApi,
  private val scheduler: ExecutionScheduler
) : UseCase.RxSingle<Feedback?, FeedbackUseCase.Params>() {
  override fun build(params: Params?): Single<Feedback?> {
    requireNotNull(params)
    return api
      .sendFeedback(params?.feedback!!)
      .compose(scheduler.highPrioritySingle())
  }

  data class Params(val feedback: String)
}


