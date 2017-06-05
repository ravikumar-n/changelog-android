package com.ravikumar.changelogmonitor.features.dashboard

import com.ravikumar.changelogmonitor.base.BasePresenter
import com.ravikumar.domain.UseCaseObserver
import com.ravikumar.domain.usecases.device.UpdateDeviceUseCase
import com.ravikumar.domain.usecases.user.GetUserUseCase
import com.ravikumar.entities.DeviceInfo
import com.ravikumar.entities.User
import timber.log.Timber
import javax.inject.Inject

class DashboardPresenter<V : DashboardContract.View>
@Inject constructor(
  private val getUserUseCase: GetUserUseCase,
  private val updateDeviceUseCase: UpdateDeviceUseCase
) : BasePresenter<V>(), DashboardContract.Presenter<V> {
  // region Init
  init {
    disposable.addAll(
      getUserUseCase.disposables,
      updateDeviceUseCase.disposables
    )
  }
  // endregion

  // region Presenter
  override fun onAttach(view: V) {
    super.onAttach(view)
    checkUserSignedIn()
  }

  override fun checkUserSignedIn() {
    getUserUseCase.execute(UserObserver())
  }

  override fun updateDeviceTokenIfRequired(firebaseToken: String) {
    updateDeviceUseCase
      .execute(
        UpdateDeviceObserver(),
        UpdateDeviceUseCase.Params(firebaseToken)
      )
  }
  // endregion

  // region Observers
  inner class UserObserver : UseCaseObserver.RxSingle<User?>() {
    override fun onSuccess(value: User?) {
      if (value == null) {
        view?.onUserNotSignedIn()
      } else {
        view?.onUserSignedIn()
      }
    }

    override fun onError(e: Throwable) {
      Timber.w(e, "User hasn't signed in!")
      view?.onUserNotSignedIn()
    }
  }

  inner class UpdateDeviceObserver : UseCaseObserver.RxMaybe<DeviceInfo?>() {
    override fun onSuccess(value: DeviceInfo?) {
      Timber.i("Device info updated")
    }

    override fun onError(e: Throwable) {
      Timber.e(e, "Failed to update the registered device")
    }
  }
  // endregion
}
