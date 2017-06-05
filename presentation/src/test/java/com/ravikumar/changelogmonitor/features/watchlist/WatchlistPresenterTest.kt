package com.ravikumar.changelogmonitor.features.watchlist

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.verify
import com.ravikumar.domain.usecases.RepositoriesUseCase
import com.ravikumar.domain.usecases.watchlist.SaveWatchlistUseCase
import com.ravikumar.entities.Meta
import com.ravikumar.entities.Repositories
import com.ravikumar.shared.TestHelper
import io.reactivex.disposables.CompositeDisposable
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyList
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class WatchlistPresenterTest {
  @Mock private lateinit var repositoriesUseCase: RepositoriesUseCase
  @Mock private lateinit var saveWatchlistUseCase: SaveWatchlistUseCase
  @Mock private lateinit var view: WatchlistContract.View

  private lateinit var presenter: WatchlistPresenter<WatchlistContract.View>

  @Before
  fun setUp() {
    given { repositoriesUseCase.disposables }.willReturn(CompositeDisposable())

    presenter = WatchlistPresenter(repositoriesUseCase, saveWatchlistUseCase)
    presenter.onAttach(view)
  }

  @Test
  fun `Should show repositories when repositories fetched successfully`() {
    val captor = argumentCaptor<WatchlistPresenter<WatchlistContract.View>.RepositoriesObserver>()

    presenter.fetchRepositories(offsetIndex = 0)
    verify(view).showLoading(anyInt())
    verify(repositoriesUseCase).execute(
      captor.capture(),
      eq(RepositoriesUseCase.Params(offsetIndex = 0))
    )

    val observer = captor.firstValue
    val fakeResponse = TestHelper().getFakeRepositoriesResponse()
    observer.onNext(fakeResponse)
    observer.onComplete()

    verify(view).showRepositories(any(), any())
  }

  @Test
  fun `Should show empty state when repositories is empty`() {
    val captor = argumentCaptor<WatchlistPresenter<WatchlistContract.View>.RepositoriesObserver>()

    presenter.fetchRepositories(offsetIndex = 0)
    verify(view).showLoading(anyInt())
    verify(repositoriesUseCase).execute(captor.capture(), any())

    val observer = captor.firstValue
    observer.onNext(Repositories(emptyList(), emptyMeta()))
    observer.onComplete()

    verify(view).showEmptyState(any(), textRes = ArgumentMatchers.anyInt())
  }

  @Test
  fun `Should show error state when failed to fetch repositories`() {
    val captor = argumentCaptor<WatchlistPresenter<WatchlistContract.View>.RepositoriesObserver>()

    presenter.fetchRepositories(offsetIndex = 0)
    verify(view).showLoading(anyInt())
    verify(repositoriesUseCase).execute(captor.capture(), any())

    captor.firstValue.onError(NullPointerException())
    verify(view).showErrorState(any(), textRes = any())
  }

  @Test
  fun `Should save watchlist`() {
    val watchlist = TestHelper().getFakeWatchListIds()
      .items
    presenter.saveWatchlist(watchlist)

    verify(view).showProgressDialog(anyInt())
    val captor = argumentCaptor<WatchlistPresenter<WatchlistContract.View>.SaveWatchlistObserver>()
    verify(saveWatchlistUseCase).execute(
      captor.capture(), eq(SaveWatchlistUseCase.Params(watchlist))
    )

    captor.firstValue.onSuccess(TestHelper().getFakeWatchList())
    verify(view).hideProgressDialog()
    verify(view).onWatchlistSavedSuccessfully(
      textRes = anyInt(),
      watchlist = anyList()
    )
  }

  @Test
  fun `Should show error state on failed to save watchlist`() {
    val watchlist = TestHelper().getFakeWatchListIds()
      .items
    presenter.saveWatchlist(watchlist)

    verify(view).showProgressDialog(anyInt())
    val captor = argumentCaptor<WatchlistPresenter<WatchlistContract.View>.SaveWatchlistObserver>()
    verify(saveWatchlistUseCase).execute(
      captor.capture(), eq(SaveWatchlistUseCase.Params(watchlist))
    )

    captor.firstValue.onError(NullPointerException())
    verify(view).hideProgressDialog()
    verify(view).onFailedToSaveWatchlist(any())
  }

  private fun emptyMeta() = Meta(totalPages = 0, currentPage = 0, offsetIndex = 0, itemsPerPage = 0)
}
