package com.ravikumar.changelogmonitor.features.feed

import com.ravikumar.changelogmonitor.R
import com.ravikumar.changelogmonitor.base.BasePresenter
import com.ravikumar.domain.UseCaseObserver
import com.ravikumar.domain.usecases.changelog.GetFeedUseCase
import com.ravikumar.domain.usecases.user.GetUserUseCase
import com.ravikumar.entities.ChangelogResponse
import com.ravikumar.entities.User
import timber.log.Timber
import javax.inject.Inject

class FeedPresenter<V : FeedContract.View>
@Inject constructor(
  private val getUserUseCase: GetUserUseCase,
  private val getFeedUseCase: GetFeedUseCase
) : BasePresenter<V>(), FeedContract.Presenter<V> {
  // region Init
  init {
    disposable.addAll(
      getUserUseCase.disposables,
      getFeedUseCase.disposables
    )
  }
  // endregion

  // region Presenter
  override fun onAttach(view: V) {
    super.onAttach(view)
    fetchFeed()
  }

  override fun fetchFeed() {
    view?.showLoading(textRes = R.string.progress_fetching_feed)
    getUserUseCase.execute({ user: User? ->
      getFeedUseCase.execute(GetFeedObserver(), GetFeedUseCase.Params(user?.id))
    }, {
      view?.hideLoading()
      view?.showErrorState(textRes = R.string.error_invalid_user)
    })
  }
  // endregion

  // region Observer
  inner class GetFeedObserver : UseCaseObserver.RxFlowable<ChangelogResponse?>() {
    override fun onNext(value: ChangelogResponse?) {
      view?.hideLoading()
      val changelogs = value?.changelogs
      val repos = value?.repositories
      if (value == null || changelogs?.isEmpty() == true || repos?.isEmpty() == true) {
        view?.showEmptyState(textRes = R.string.empty_feed)
      } else {
        view?.showFeed(changelogs!!, repos!!)
      }
    }

    override fun onError(e: Throwable) {
      Timber.e(e, "Failed to fetch the feed")
      view?.hideLoading()
      view?.showErrorState(textRes = R.string.error_failed_to_fetch_feed)
    }
  }
  // endregion
}
