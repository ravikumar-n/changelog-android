package com.ravikumar.changelogmonitor.features.info.settings

import com.ravikumar.changelogmonitor.base.BasePresenter
import com.ravikumar.changelogmonitor.features.info.settings.ChangelogSettingsContract.Presenter
import com.ravikumar.changelogmonitor.features.info.settings.ChangelogSettingsContract.View
import com.ravikumar.domain.usecases.feedback.FeedbackUseCase
import timber.log.Timber
import javax.inject.Inject

class ChangelogSettingsPresenter<V : View>
@Inject constructor(
  private val feedbackUseCase: dagger.Lazy<FeedbackUseCase>
) : BasePresenter<V>(), Presenter<V> {

  // region Presenter
  override fun sendFeedback(feedback: String) {
    disposable.add(feedbackUseCase.get().disposables)
    feedbackUseCase.get()
      .execute({
        view?.onFeedbackSent()
      }, { throwable ->
        Timber.e(throwable, "Failed to send the feedback")
        view?.onFailedToSendFeedback()
      },
        FeedbackUseCase.Params(feedback)
      )
  }
  // endregion
}
