package com.ravikumar.changelogmonitor.features.dashboard

import com.nhaarman.mockito_kotlin.anyOrNull
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.verify
import com.ravikumar.domain.usecases.device.UpdateDeviceUseCase
import com.ravikumar.domain.usecases.user.GetUserUseCase
import com.ravikumar.shared.TestHelper
import io.reactivex.disposables.CompositeDisposable
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class DashboardPresenterTest {
  @Mock private lateinit var getUserUseCase: GetUserUseCase
  @Mock private lateinit var updateDeviceUseCase: UpdateDeviceUseCase
  @Mock private lateinit var view: DashboardContract.View

  private lateinit var presenter: DashboardPresenter<DashboardContract.View>

  @Before
  fun setUp() {
    given { getUserUseCase.disposables }.willReturn(CompositeDisposable())
    given { updateDeviceUseCase.disposables }.willReturn(CompositeDisposable())

    presenter = DashboardPresenter(getUserUseCase, updateDeviceUseCase)
  }

  @Test
  fun `Check user not signed in called when no user found`() {
    presenter.onAttach(view)

    val captor = argumentCaptor<DashboardPresenter<DashboardContract.View>.UserObserver>()
    verify(getUserUseCase).execute(captor.capture(), anyOrNull())

    captor.firstValue.onError(NullPointerException())
    verify(view).onUserNotSignedIn()
  }

  @Test
  fun `Check user not signed in is called for a invalid user`() {
    presenter.onAttach(view)

    val captor = argumentCaptor<DashboardPresenter<DashboardContract.View>.UserObserver>()
    verify(getUserUseCase).execute(captor.capture(), anyOrNull())

    captor.firstValue.onSuccess(null)
    verify(view).onUserNotSignedIn()
  }

  @Test
  fun `Check user signed in is called for a valid user`() {
    presenter.onAttach(view)

    val captor = argumentCaptor<DashboardPresenter<DashboardContract.View>.UserObserver>()
    verify(getUserUseCase).execute(captor.capture(), anyOrNull())

    captor.firstValue.onSuccess(TestHelper().getFakeUser())
    verify(view).onUserSignedIn()
  }
}
