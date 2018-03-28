package com.ravikumar.changelogmonitor.features.changelogdetail

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.verify
import com.ravikumar.changelogmonitor.features.changelogdetail.ChangelogDetailContract.View
import com.ravikumar.domain.usecases.changelog.GetChangelogDetailUseCase
import com.ravikumar.domain.usecases.watchlist.GetRepositoryUseCase
import com.ravikumar.entities.ChangelogDetail
import com.ravikumar.shared.REPO_UUID
import com.ravikumar.shared.TestHelper
import io.reactivex.disposables.CompositeDisposable
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ChangelogDetailPresenterTest {
  @Mock private lateinit var changelogDetailUseCase: GetChangelogDetailUseCase
  @Mock private lateinit var repositoryUseCase: GetRepositoryUseCase
  @Mock private lateinit var view: ChangelogDetailContract.View

  private lateinit var presenter: ChangelogDetailPresenter<View>

  @Before
  fun setUp() {
    given { changelogDetailUseCase.disposables }.willReturn(CompositeDisposable())
    given { repositoryUseCase.disposables }.willReturn(CompositeDisposable())

    presenter = ChangelogDetailPresenter(changelogDetailUseCase, repositoryUseCase)
    presenter.onAttach(view)
  }

  @Test
  fun `Should show repository info`() {
    presenter.fetchRepository(REPO_UUID)

    val captor = argumentCaptor<ChangelogDetailPresenter<View>.RepoObserver>()
    verify(repositoryUseCase).execute(captor.capture(), any())

    captor.firstValue.onSuccess(TestHelper().getFakeRepositoriesResponse().repositories.first())
    verify(view).showRepository(any())
  }

  @Test
  fun `Should show changelog detail`() {
    presenter.fetchChangelogDetail(repoId = REPO_UUID, repoTitle = "Test")
    verify(view).showLoading(any())

    val captor = argumentCaptor<ChangelogDetailPresenter<View>.ChangelogDetailObserver>()
    verify(changelogDetailUseCase).execute(
      captor.capture(),
      eq(GetChangelogDetailUseCase.Params(REPO_UUID))
    )

    captor.firstValue.onNext(TestHelper().getFakeChangelogDetail())
    verify(view).hideLoading()
    verify(view).showChangelog(any())
  }

  @Test
  fun `Should show short changelog`() {
    presenter.fetchChangelogDetail(repoId = REPO_UUID, repoTitle = "jetbrains/kotlin")
    verify(view).showLoading(any())

    val captor = argumentCaptor<ChangelogDetailPresenter<View>.ChangelogDetailObserver>()
    verify(changelogDetailUseCase).execute(
      captor.capture(),
      eq(GetChangelogDetailUseCase.Params(REPO_UUID, true))
    )

    captor.firstValue.onNext(TestHelper().getFakeChangelogDetail())
    verify(view).hideLoading()
    verify(view).showChangelog(any())
  }

  @Test
  fun `Should show empty state when changelog is empty`() {
    presenter.fetchChangelogDetail(repoId = REPO_UUID, repoTitle = "Test")
    verify(view).showLoading(any())

    val captor = argumentCaptor<ChangelogDetailPresenter<View>.ChangelogDetailObserver>()
    verify(changelogDetailUseCase).execute(
      captor.capture(),
      eq(GetChangelogDetailUseCase.Params(REPO_UUID))
    )

    captor.firstValue.onNext(ChangelogDetail(repoId = REPO_UUID, changelog = ""))
    verify(view).hideLoading()
    verify(view).showEmptyState(drawableRes = anyInt(), textRes = anyInt())
  }

  @Test
  fun `Should show error state when failed to fetch changelog detail`() {
    presenter.fetchChangelogDetail(repoId = REPO_UUID, repoTitle = "Test")
    verify(view).showLoading(any())

    val captor = argumentCaptor<ChangelogDetailPresenter<View>.ChangelogDetailObserver>()
    verify(changelogDetailUseCase).execute(
      captor.capture(),
      eq(GetChangelogDetailUseCase.Params(REPO_UUID))
    )

    captor.firstValue.onError(NullPointerException())
    verify(view).hideLoading()
    verify(view).showErrorState(drawableRes = any(), textRes = any())
  }
}
