package com.ravikumar.changelogmonitor.features.feed

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.anyVararg
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.verify
import com.ravikumar.domain.usecases.changelog.GetFeedUseCase
import com.ravikumar.domain.usecases.user.GetUserUseCase
import com.ravikumar.entities.ChangelogResponse
import com.ravikumar.entities.User
import com.ravikumar.shared.TestHelper
import io.reactivex.disposables.CompositeDisposable
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class FeedPresenterTest {
  @Mock private lateinit var getUserUseCase: GetUserUseCase
  @Mock private lateinit var getFeedUseCase: GetFeedUseCase
  @Mock private lateinit var view: FeedContract.View

  private lateinit var presenter: FeedPresenter<FeedContract.View>

  @Before
  fun setUp() {
    given { getUserUseCase.disposables }.willReturn(CompositeDisposable())
    given { getFeedUseCase.disposables }.willReturn(CompositeDisposable())

    presenter = FeedPresenter(getUserUseCase, getFeedUseCase)
    presenter.onAttach(view)
  }

  @Test
  fun `Should load feed for the user`() {
    verify(view).showLoading(any())
    val userCaptor = argumentCaptor<(User?) -> Unit>()
    verify(getUserUseCase).execute(userCaptor.capture(), any(), anyVararg())

    val feedCaptor = argumentCaptor<FeedPresenter<FeedContract.View>.GetFeedObserver>()
    val user = TestHelper().getFakeUser()
    userCaptor.firstValue.invoke(user)
    verify(getFeedUseCase).execute(feedCaptor.capture(), eq(GetFeedUseCase.Params(user.id)))

    feedCaptor.firstValue.onNext(TestHelper().getFakeFeed())
    verify(view).hideLoading()
    verify(view).showFeed(any(), any())
  }

  @Test
  fun `Should load empty state when there is no feeds`() {
    verify(view).showLoading(any())
    val userCaptor = argumentCaptor<(User?) -> Unit>()
    verify(getUserUseCase).execute(userCaptor.capture(), any(), anyVararg())

    val feedCaptor = argumentCaptor<FeedPresenter<FeedContract.View>.GetFeedObserver>()
    val user = TestHelper().getFakeUser()
    userCaptor.firstValue.invoke(user)
    verify(getFeedUseCase).execute(feedCaptor.capture(), eq(GetFeedUseCase.Params(user.id)))

    feedCaptor.firstValue.onNext(ChangelogResponse(emptyList(), emptyList()))
    verify(view).hideLoading()
    verify(view).showEmptyState(any(), textRes = any())
  }

  @Test
  fun `Should load error state when failed to fetch feeds`() {
    verify(view).showLoading(any())
    val userCaptor = argumentCaptor<(User?) -> Unit>()
    verify(getUserUseCase).execute(userCaptor.capture(), any(), anyVararg())

    val feedCaptor = argumentCaptor<FeedPresenter<FeedContract.View>.GetFeedObserver>()
    val user = TestHelper().getFakeUser()
    userCaptor.firstValue.invoke(user)
    verify(getFeedUseCase).execute(feedCaptor.capture(), eq(GetFeedUseCase.Params(user.id)))

    feedCaptor.firstValue.onError(NullPointerException())
    verify(view).hideLoading()
    verify(view).showErrorState(any(), textRes = any())
  }
}
