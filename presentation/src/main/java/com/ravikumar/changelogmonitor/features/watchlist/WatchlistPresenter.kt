package com.ravikumar.changelogmonitor.features.watchlist

import com.ravikumar.changelogmonitor.R
import com.ravikumar.changelogmonitor.base.BasePresenter
import com.ravikumar.changelogmonitor.framework.extensions.errorMessage
import com.ravikumar.domain.UseCaseObserver
import com.ravikumar.domain.usecases.RepositoriesUseCase
import com.ravikumar.domain.usecases.watchlist.SaveWatchlistUseCase
import com.ravikumar.entities.Repositories
import com.ravikumar.entities.Repository
import com.ravikumar.entities.Watchlist
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

class WatchlistPresenter<V : WatchlistContract.View>
@Inject constructor(
  private val repositoriesUseCase: RepositoriesUseCase,
  private val saveWatchlistUseCase: SaveWatchlistUseCase
) : BasePresenter<V>(), WatchlistContract.Presenter<V> {
  // region Variables
  private var repositories: MutableList<Repository> = mutableListOf()
  // endregion

  // region Init
  init {
    disposable.addAll(
      repositoriesUseCase.disposables,
      saveWatchlistUseCase.disposables
    )
  }
  // endregion

  // region Presenter
  override fun fetchRepositories(offsetIndex: Int) {
    if (repositories.isEmpty()) {
      view?.showLoading(R.string.progress_fetching_repositories)
    }
    repositoriesUseCase
      .execute(RepositoriesObserver(), RepositoriesUseCase.Params(offsetIndex))
  }

  override fun saveWatchlist(itemsToWatch: List<UUID>) {
    view?.showProgressDialog(R.string.progress_syncing_your_changes)
    saveWatchlistUseCase.execute(
      SaveWatchlistObserver(),
      SaveWatchlistUseCase.Params(itemsToWatch)
    )
  }
  // endregion

  // region Observers
  inner class RepositoriesObserver : UseCaseObserver.RxObservable<Repositories?>() {
    override fun onNext(value: Repositories?) {
      if (repositories.isEmpty()) {
        if (value == null || value.repositories.isEmpty()) {
          view?.showEmptyState(textRes = R.string.empty_repositories)
        }
      }

      value?.let {
        val newRepositories = it.repositories
        repositories.addAll(newRepositories)
        view?.showRepositories(newRepositories, value.meta)
      }
    }

    override fun onError(e: Throwable) {
      Timber.e(e, "Failed to retrieve repositories")
      view?.showErrorState(textRes = R.string.error_failed_to_fetch_repositories)
    }
  }

  inner class SaveWatchlistObserver : UseCaseObserver.RxSingle<Watchlist?>() {
    override fun onSuccess(value: Watchlist?) {
      view?.run {
        hideProgressDialog()
        onWatchlistSavedSuccessfully(
          R.string.success_sync_watchlist,
          value?.items ?: emptyList()
        )
      }
    }

    override fun onError(e: Throwable) {
      Timber.e(e, "Failed to sync the changes")
      view?.run {
        hideProgressDialog()
        onFailedToSaveWatchlist(Pair(R.string.error_sync_watchlist, e.errorMessage()))
      }
    }
  }
  // endregion
}
