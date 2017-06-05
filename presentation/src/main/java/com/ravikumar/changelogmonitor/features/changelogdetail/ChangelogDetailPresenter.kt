package com.ravikumar.changelogmonitor.features.changelogdetail

import com.ravikumar.changelogmonitor.R
import com.ravikumar.changelogmonitor.base.BasePresenter
import com.ravikumar.domain.UseCaseObserver
import com.ravikumar.domain.usecases.changelog.GetChangelogDetailUseCase
import com.ravikumar.domain.usecases.watchlist.GetRepositoryUseCase
import com.ravikumar.entities.ChangelogDetail
import com.ravikumar.entities.Repository
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

class ChangelogDetailPresenter<V : ChangelogDetailContract.View>
@Inject constructor(
  private val changelogDetailUseCase: GetChangelogDetailUseCase,
  private val repositoryUseCase: GetRepositoryUseCase
) : BasePresenter<V>(),
  ChangelogDetailContract.Presenter<V> {
  // region Init
  init {
    disposable.addAll(
      changelogDetailUseCase.disposables,
      repositoryUseCase.disposables
    )
  }
  // endregion

  // region Presenter
  override fun fetchRepository(repoId: UUID) {
    repositoryUseCase.execute(RepoObserver(), GetRepositoryUseCase.Params(repoId))
  }

  override fun fetchChangelogDetail(
    repoId: UUID,
    repoTitle: String?
  ) {
    view?.showLoading(textRes = R.string.progress_fetching_changelog_detail)
    // TODO: Should lazy load text
    if (repoTitle != null && (
        repoTitle.equals("jetbrains/kotlin", true)
          || repoTitle.equals("angular/angular.js", true)
        )
    ) {
      changelogDetailUseCase.execute(
        ChangelogDetailObserver(),
        GetChangelogDetailUseCase.Params(repoId, true)
      )
      view?.showUserHint(R.string.prompt_short_changelog)
    } else {
      changelogDetailUseCase.execute(
        ChangelogDetailObserver(),
        GetChangelogDetailUseCase.Params(repoId)
      )
    }
  }
  // endregion

  // region Observer
  private inner class RepoObserver : UseCaseObserver.RxMaybe<Repository>() {
    override fun onSuccess(value: Repository) {
      view?.showRepository(value)
    }

    override fun onError(e: Throwable) {
      Timber.e("No repository present locally")
    }
  }

  private inner class ChangelogDetailObserver : UseCaseObserver.RxFlowable<ChangelogDetail>() {
    override fun onNext(value: ChangelogDetail) {
      view?.hideLoading()
      if (value.changelog.isEmpty()) {
        view?.showEmptyState(textRes = R.string.empty_feed_detail)
      } else {
        view?.showChangelog(value.changelog)
      }
    }

    override fun onError(e: Throwable) {
      Timber.e(e, "Failed to fetch the feed detail")
      view?.hideLoading()
      view?.showErrorState(textRes = R.string.error_failed_to_fetch_detail)
    }
  }
  // endregion
}
