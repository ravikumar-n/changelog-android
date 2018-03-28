package com.ravikumar.changelogmonitor.features.subscriptions

import com.android.billingclient.api.Purchase
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.anyVararg
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.verify
import com.ravikumar.changelogmonitor.features.subscriptions.SubscriptionContract.View
import com.ravikumar.domain.usecases.subscriptions.AddSubscriptionUseCase
import com.ravikumar.domain.usecases.subscriptions.GetSubscriptionUseCase
import com.ravikumar.domain.usecases.subscriptions.RemoveSubscriptionUseCase
import com.ravikumar.domain.usecases.user.ConnectUserUseCase
import com.ravikumar.domain.usecases.user.GetUserUseCase
import com.ravikumar.entities.ChangelogSubscription
import com.ravikumar.entities.SubscriptionResponse
import com.ravikumar.entities.User
import com.ravikumar.entities.UserResponse
import com.ravikumar.shared.TestHelper
import io.reactivex.disposables.CompositeDisposable
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SubscriptionsPresenterTest {
  @Mock private lateinit var getUserUseCase: GetUserUseCase
  @Mock private lateinit var getSubscriptionUseCase: GetSubscriptionUseCase
  @Mock private lateinit var addSubscriptionUseCase: AddSubscriptionUseCase
  @Mock private lateinit var connectUserUseCase: ConnectUserUseCase
  @Mock private lateinit var removeSubscriptionUseCase: RemoveSubscriptionUseCase
  @Mock private lateinit var view: View

  private lateinit var presenter: SubscriptionsPresenter<View>

  @Before
  fun setUp() {
    given { getUserUseCase.disposables }.willReturn(CompositeDisposable())
    given { getSubscriptionUseCase.disposables }.willReturn(CompositeDisposable())
    given { addSubscriptionUseCase.disposables }.willReturn(CompositeDisposable())
    given { connectUserUseCase.disposables }.willReturn(CompositeDisposable())
    given { removeSubscriptionUseCase.disposables }.willReturn(CompositeDisposable())

    presenter = SubscriptionsPresenter(
      getUserUseCase, getSubscriptionUseCase, addSubscriptionUseCase,
      connectUserUseCase, removeSubscriptionUseCase
    )
  }

  @Test
  fun `Should fetch user info onAttach`() {
    presenter.onAttach(view)

    val userCaptor = argumentCaptor<(User?) -> Unit>()
    verify(getUserUseCase).execute(userCaptor.capture(), any(), anyVararg())
  }

  @Test
  fun `Should register a new purchase and update the view`() {
    mockUser()

    presenter.checkAndRegisterPurchase(getFakePurchase())
    verify(view).showLoading(any())

    val subscriptionCaptor = argumentCaptor<(SubscriptionResponse?) -> Unit>()
    verify(addSubscriptionUseCase).execute(subscriptionCaptor.capture(), any(), anyVararg())

    subscriptionCaptor.firstValue.invoke(TestHelper().getFakeSubscriptionResponse())
    verify(view).hideLoading()
    verify(view).onSubscriptionPurchased(any())
    verify(view).showSubscription(any())
  }

  @Test
  fun `Should display a existing subscription info if user has one`() {
    mockProUser()

    presenter.checkAndRegisterPurchase(getFakePurchase())

    val subscriptionCaptor = argumentCaptor<(ChangelogSubscription?) -> Unit>()
    verify(getSubscriptionUseCase).execute(subscriptionCaptor.capture(), any(), anyVararg())

    subscriptionCaptor.firstValue.invoke(TestHelper().getFakeSubscriptionResponse().subscription)
    verify(view).showSubscription(any())
  }

  @Test
  fun `Should display free plan when user has no subscriptions`() {
    mockProUser()

    presenter.checkAndRegisterPurchase(getFakePurchase())

    val subscriptionCaptor = argumentCaptor<(ChangelogSubscription?) -> Unit>()
    verify(getSubscriptionUseCase).execute(subscriptionCaptor.capture(), any(), anyVararg())

    subscriptionCaptor.firstValue.invoke(null)
    verify(view).showFreePlan()
  }

  @Test
  fun `Should show error state when subscription registration failed`() {
    mockUser()

    presenter.checkAndRegisterPurchase(getFakePurchase())
    verify(view).showLoading(any())

    val errorCaptor = argumentCaptor<(Throwable) -> Unit>()
    verify(addSubscriptionUseCase).execute(any(), errorCaptor.capture(), anyVararg())

    errorCaptor.firstValue.invoke(NullPointerException())
    verify(view).hideLoading()
    verify(view).onFailedToRegisterSubscription(any())
  }

  @Test
  fun `Should remove subscription`() {
    mockProUser()

    presenter.removeSubscriptionsIfAny()

    val captor = argumentCaptor<(User?) -> Unit>()
    verify(removeSubscriptionUseCase).execute(captor.capture(), any(), anyVararg())

    captor.firstValue.invoke(null)
    verify(view).onSubscriptionRemoved()
  }

  @Test
  fun `Should connect a guest user with valid google credential`() {
    mockUser()

    presenter.connectUser("123", "Arya Stark", "arya@winterfell.com")
    val errorCaptor = argumentCaptor<(Throwable) -> Unit>()
    verify(connectUserUseCase).execute(any(), errorCaptor.capture(), anyVararg())

    errorCaptor.firstValue.invoke(NullPointerException())
    verify(view).onGoogleSignInFailed()
  }

  @Test
  fun `Should show error when failed to connect a guest user with google account`() {
    mockUser()

    presenter.connectUser("123", "Arya Stark", "arya@winterfell.com")
    val captor = argumentCaptor<(UserResponse?) -> Unit>()
    verify(connectUserUseCase).execute(captor.capture(), any(), anyVararg())

    captor.firstValue.invoke(TestHelper().getFakeUserResponse())
    verify(view).onGoogleSignInSuccess(any())
  }

  private fun mockUser() {
    presenter.onAttach(view)

    val userCaptor = argumentCaptor<(User?) -> Unit>()
    verify(getUserUseCase).execute(userCaptor.capture(), any(), anyVararg())

    val user = TestHelper().getFakeUser()
    userCaptor.firstValue.invoke(user)
  }

  private fun mockProUser() {
    presenter.onAttach(view)

    val userCaptor = argumentCaptor<(User?) -> Unit>()
    verify(getUserUseCase).execute(userCaptor.capture(), any(), anyVararg())

    val user = TestHelper().getFakeProUser()
    userCaptor.firstValue.invoke(user)
  }

  private fun getFakePurchase(): Purchase {
    return Purchase(
      "{\"orderId\":\"GPA.33284-02235-1074-327341\",\"packageName\":\"com.ravikumar.changelogmonitor\",\"productId\":\"premium_half_yearly\",\"purchaseTime\":1522195600322,\"purchaseState\":0,\"purchaseToken\":\"this_is_a_purhcase_token\",\"autoRenewing\":true}",
      "signature"
    )
  }
}
