package com.ravikumar.changelogmonitor.features.info

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.anyOrNull
import com.nhaarman.mockito_kotlin.anyVararg
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.verify
import com.ravikumar.domain.usecases.SignOutUseCase
import com.ravikumar.domain.usecases.subscriptions.GetSubscriptionUseCase
import com.ravikumar.domain.usecases.user.GetUserUseCase
import com.ravikumar.entities.ChangelogSubscription
import com.ravikumar.entities.User
import com.ravikumar.shared.TestHelper
import io.reactivex.disposables.CompositeDisposable
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.util.UUID

@RunWith(MockitoJUnitRunner::class)
class InfoPresenterTest {
  @Mock private lateinit var getUserUseCase: GetUserUseCase
  @Mock private lateinit var getSubscriptionUseCase: GetSubscriptionUseCase
  @Mock private lateinit var signOutUseCase: SignOutUseCase
  @Mock private lateinit var view: InfoContract.View

  private lateinit var presenter: InfoPresenter<InfoContract.View>

  @Before
  fun setUp() {
    given { getUserUseCase.disposables }.willReturn(CompositeDisposable())
    given { getSubscriptionUseCase.disposables }.willReturn(CompositeDisposable())
    given { signOutUseCase.disposables }.willReturn(CompositeDisposable())

    presenter = InfoPresenter(getUserUseCase, getSubscriptionUseCase, signOutUseCase)
    presenter.onAttach(view)
  }

  @Test
  fun `Should display user info`() {
    val userCaptor = argumentCaptor<(User?) -> Unit>()
    verify(getUserUseCase).execute(userCaptor.capture(), any(), anyVararg())

    val user = TestHelper().getFakeUser()
    userCaptor.firstValue.invoke(user)
    verify(view).showUserInfo(eq(user))
  }

  @Test
  fun `Should fetch subscription info when user has a subscription`() {
    val userCaptor = argumentCaptor<(User?) -> Unit>()
    verify(getUserUseCase).execute(userCaptor.capture(), any(), anyVararg())

    val user = TestHelper().getFakeProUser()
    userCaptor.firstValue.invoke(user)
    verify(getSubscriptionUseCase).execute(any(), any(), any())
  }

  @Test
  fun `Should display free plan when user has no subscriptions`() {
    presenter.fetchSubscriptionInfo(UUID.randomUUID())

    val captor = argumentCaptor<(ChangelogSubscription?) -> Unit>()
    verify(getSubscriptionUseCase).execute(captor.capture(), any(), any())

    captor.firstValue.invoke(null)
    verify(view).showFreePlan()
  }

  @Test
  fun `Should show subscription when user has one`() {
    presenter.fetchSubscriptionInfo(UUID.randomUUID())

    val captor = argumentCaptor<(ChangelogSubscription?) -> Unit>()
    verify(getSubscriptionUseCase).execute(captor.capture(), any(), any())

    captor.firstValue.invoke(ChangelogSubscription())
    verify(view).showSubscription(any())
  }

  @Test
  fun `Should sign out user`() {
    presenter.signout()

    val captor = argumentCaptor<() -> Unit>()
    verify(signOutUseCase).execute(captor.capture(), anyOrNull())

    captor.firstValue.invoke()
    verify(view).onUserSignedOut()
  }
}
