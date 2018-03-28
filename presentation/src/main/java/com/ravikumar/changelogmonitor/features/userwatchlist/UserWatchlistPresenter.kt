package com.ravikumar.changelogmonitor.features.userwatchlist

import com.ravikumar.changelogmonitor.R
import com.ravikumar.changelogmonitor.base.BasePresenter
import com.ravikumar.domain.UseCaseObserver
import com.ravikumar.domain.usecases.user.GetUserUseCase
import com.ravikumar.domain.usecases.watchlist.GetWatchListUseCase
import com.ravikumar.entities.Repository
import timber.log.Timber
import javax.inject.Inject

class UserWatchlistPresenter<V : UserWatchlistContract.View>
@Inject constructor(
  private val getUserUseCase: GetUserUseCase,
  private val getWatchlistUseCase: GetWatchListUseCase
) : BasePresenter<V>(), UserWatchlistContract.Presenter<V> {
  // region Variables
  var watchlist: MutableList<Repository> = mutableListOf()
  // endregion

  // region Init
  init {
    disposable.addAll(
      getUserUseCase.disposables,
      getWatchlistUseCase.disposables
    )
  }
  // endregion

  // region Presenter
  override fun onAttach(view: V) {
    super.onAttach(view)
    fetchWatchlist()
  }

  override fun fetchWatchlist() {
    view?.showLoading(R.string.progress_fetching_user_watchlist)
    getUserUseCase.execute({
      getWatchlistUseCase.execute(UserWatchlistObserver())
    }, {
      view?.hideLoading()
      view?.showErrorState(textRes = R.string.error_invalid_user)
    })
  }
  // endregion

  // region Observers
  inner class UserWatchlistObserver : UseCaseObserver.RxFlowable<List<Repository>?>() {
    override fun onNext(value: List<Repository>?) {
      view?.hideLoading()
      if (value == null || value.isEmpty()) {
        watchlist.clear()
        view?.showEmptyState(textRes = R.string.empty_user_watchlist)
      } else {
        view?.showWatchlist(value)
        watchlist.clear()
        watchlist.addAll(value)
      }
    }

    override fun onError(e: Throwable) {
      Timber.e(e, "Failed to fetch the watchlist")
      view?.hideLoading()
      view?.showErrorState(textRes = R.string.error_failed_to_fetch_user_watchlist)
    }
  }
  // endregion
}
