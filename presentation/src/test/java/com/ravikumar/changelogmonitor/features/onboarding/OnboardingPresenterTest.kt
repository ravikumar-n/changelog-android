package com.ravikumar.changelogmonitor.features.onboarding

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.anyOrNull
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.verify
import com.ravikumar.domain.usecases.device.RegisterDeviceUseCase
import com.ravikumar.domain.usecases.signin.GoogleSignInUseCase
import com.ravikumar.domain.usecases.signin.GuestSignInUseCase
import com.ravikumar.shared.TestHelper
import io.reactivex.disposables.CompositeDisposable
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class OnboardingPresenterTest {
  @Mock private lateinit var guestSignInUseCase: GuestSignInUseCase
  @Mock private lateinit var googleSignInUseCase: GoogleSignInUseCase
  @Mock private lateinit var registerDeviceUseCase: RegisterDeviceUseCase
  @Mock private lateinit var view: OnboardingContract.View

  private lateinit var presenter: OnboardingPresenter<OnboardingContract.View>

  @Before
  fun setUp() {
    given { guestSignInUseCase.disposables }.willReturn(CompositeDisposable())
    given { googleSignInUseCase.disposables }.willReturn(CompositeDisposable())
    given { registerDeviceUseCase.disposables }.willReturn(CompositeDisposable())

    presenter = OnboardingPresenter(guestSignInUseCase, googleSignInUseCase, registerDeviceUseCase)
    presenter.onAttach(view)
  }

  @Test
  fun `Should create a valid guest user`() {
    presenter.createGuestUser()

    verify(view).showLoading(any())

    val captor = argumentCaptor<OnboardingPresenter<OnboardingContract.View>.GuestUserObserver>()
    verify(guestSignInUseCase).execute(captor.capture(), anyOrNull())

    val fakeResponse = TestHelper().getFakeUserResponse()
    captor.firstValue.onSuccess(fakeResponse)
    verify(view).onGuestUserCreated(fakeResponse.user)
  }

  @Test
  fun `Should show error state for a invalid guest user`() {
    presenter.createGuestUser()

    verify(view).showLoading(any())
    val captor = argumentCaptor<OnboardingPresenter<OnboardingContract.View>.GuestUserObserver>()
    verify(guestSignInUseCase).execute(captor.capture(), anyOrNull())

    captor.firstValue.onSuccess(null)
    verify(view).hideLoading()
    verify(view).showErrorState(any(), textRes = any())
  }

  @Test
  fun `Should show error state when guest user creation failed`() {
    presenter.createGuestUser()

    verify(view).showLoading(any())
    val captor = argumentCaptor<OnboardingPresenter<OnboardingContract.View>.GuestUserObserver>()
    verify(guestSignInUseCase).execute(captor.capture(), anyOrNull())

    captor.firstValue.onError(NullPointerException())
    verify(view).hideLoading()
    verify(view).showErrorState(any(), textRes = any())
  }

  @Test
  fun `Should create a valid google user`() {
    val userName = "Arya Stark"
    presenter.createUser("123", userName, "arya@winterfell.com")

    verify(view).onGoogleSignIn(userName)
    val captor = argumentCaptor<OnboardingPresenter<OnboardingContract.View>.GoogleSignInObserver>()
    verify(googleSignInUseCase).execute(captor.capture(), any())

    val fakeResponse = TestHelper().getFakeUserResponse()
    captor.firstValue.onSuccess(fakeResponse)
    verify(view).onGoogleSignIn(userName)
  }

  @Test
  fun `Should show error state for a invalid google user`() {
    presenter.createUser("123", "Arya Stark", "arya@winterfell.com")

    verify(view).onGoogleSignIn(any())
    val captor = argumentCaptor<OnboardingPresenter<OnboardingContract.View>.GoogleSignInObserver>()
    verify(googleSignInUseCase).execute(captor.capture(), any())

    captor.firstValue.onSuccess(null)
    verify(view).hideLoading()
    verify(view).showErrorState(any(), textRes = any())
  }

  @Test
  fun `Should show error state on failed to sign in with google`() {
    presenter.createUser("123", "Arya Stark", "arya@winterfell.com")

    verify(view).onGoogleSignIn(any())
    val captor = argumentCaptor<OnboardingPresenter<OnboardingContract.View>.GoogleSignInObserver>()
    verify(googleSignInUseCase).execute(captor.capture(), any())

    captor.firstValue.onError(NullPointerException())
    verify(view).hideLoading()
    verify(view).showErrorState(any(), textRes = any())
  }

  @Test
  fun `Should register the device`() {
    val deviceInfo = TestHelper().getFakeDeviceInfo()
    presenter.registerDevice(deviceInfo)

    verify(view).showLoading(any())
    val captor =
      argumentCaptor<OnboardingPresenter<OnboardingContract.View>.RegisterDeviceObserver>()
    verify(registerDeviceUseCase).execute(captor.capture(), any())

    captor.firstValue.onSuccess(deviceInfo)
    verify(view).hideLoading()
    verify(view).onDeviceRegistered(any())
  }

  @Test
  fun `Should show error state on device registration returns invalid response`() {
    val deviceInfo = TestHelper().getFakeDeviceInfo()
    presenter.registerDevice(deviceInfo)

    verify(view).showLoading(any())
    val captor =
      argumentCaptor<OnboardingPresenter<OnboardingContract.View>.RegisterDeviceObserver>()
    verify(registerDeviceUseCase).execute(captor.capture(), any())

    captor.firstValue.onSuccess(null)
    verify(view).hideLoading()
    verify(view).showErrorState(any(), textRes = any())
  }

  @Test
  fun `Should show error state on failed to register the device`() {
    val deviceInfo = TestHelper().getFakeDeviceInfo()
    presenter.registerDevice(deviceInfo)

    verify(view).showLoading(any())
    val captor =
      argumentCaptor<OnboardingPresenter<OnboardingContract.View>.RegisterDeviceObserver>()
    verify(registerDeviceUseCase).execute(captor.capture(), any())

    captor.firstValue.onError(NullPointerException())
    verify(view).hideLoading()
    verify(view).showErrorState(any(), textRes = any())
  }
}

