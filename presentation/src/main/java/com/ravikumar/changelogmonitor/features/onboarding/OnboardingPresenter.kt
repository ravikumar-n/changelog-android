package com.ravikumar.changelogmonitor.features.onboarding

import com.ravikumar.changelogmonitor.R
import com.ravikumar.changelogmonitor.base.BasePresenter
import com.ravikumar.domain.UseCaseObserver
import com.ravikumar.domain.usecases.device.RegisterDeviceUseCase
import com.ravikumar.domain.usecases.signin.GoogleSignInUseCase
import com.ravikumar.domain.usecases.signin.GuestSignInUseCase
import com.ravikumar.entities.DeviceInfo
import com.ravikumar.entities.User
import com.ravikumar.entities.UserResponse
import timber.log.Timber
import javax.inject.Inject

class OnboardingPresenter<V : OnboardingContract.View>
@Inject constructor(
  private val guestSignInUseCase: GuestSignInUseCase,
  private val googleSignInUseCase: GoogleSignInUseCase,
  private val registerDeviceUseCase: RegisterDeviceUseCase
) : BasePresenter<V>(), OnboardingContract.Presenter<V> {
  // region Variables
  var user: User? = null
  // endregion

  // region Init
  init {
    disposable.addAll(
      guestSignInUseCase.disposables,
      registerDeviceUseCase.disposables,
      googleSignInUseCase.disposables
    )
  }
  // endregion

  // region Presenter
  override fun createGuestUser() {
    view?.showLoading(R.string.progress_create_guest_user)
    guestSignInUseCase.execute(GuestUserObserver())
  }

  override fun createUser(
    idToken: String,
    userName: String,
    email: String
  ) {
    view?.onGoogleSignIn(userName)
    googleSignInUseCase.execute(GoogleSignInObserver(), GoogleSignInUseCase.Params(idToken, email))
  }

  override fun registerDevice(deviceInfo: DeviceInfo) {
    view?.showLoading(R.string.progress_register_device)
    registerDeviceUseCase.execute(
      RegisterDeviceObserver(),
      RegisterDeviceUseCase.Params(deviceInfo)
    )
  }
  // endregion

  // region Observers
  inner class GuestUserObserver : UseCaseObserver.RxSingle<UserResponse?>() {
    override fun onSuccess(value: UserResponse?) {
      if (value == null) {
        failedToCreateGuestUser()
      } else {
        user = value.user
        view?.onGuestUserCreated(user!!)
      }
    }

    override fun onError(e: Throwable) {
      Timber.e(e, "Guest user creation failed.")
      failedToCreateGuestUser()
    }
  }

  inner class GoogleSignInObserver : UseCaseObserver.RxSingle<UserResponse?>() {
    override fun onSuccess(value: UserResponse?) {
      if (value == null) {
        failedToCreateUser()
      } else {
        user = value.user
        view?.onUserCreated(user!!)
      }
    }

    override fun onError(e: Throwable) {
      Timber.e(e, "Google sign in failed :(")
      failedToCreateUser()
    }
  }

  inner class RegisterDeviceObserver : UseCaseObserver.RxSingle<DeviceInfo?>() {
    override fun onSuccess(value: DeviceInfo?) {
      if (value == null) {
        failedToRegisterDevice()
      } else {
        view?.hideLoading()
        view?.onDeviceRegistered(value)
      }
    }

    override fun onError(e: Throwable) {
      Timber.e(e, "Failed to register the device")
      failedToRegisterDevice()
    }
  }
  // endregion

  // region Private
  private fun failedToCreateGuestUser() {
    onFailure(R.string.error_failed_to_create_guest_user)
  }

  private fun failedToCreateUser() {
    onFailure(R.string.error_failed_to_create_user)
  }

  private fun failedToRegisterDevice() {
    onFailure(R.string.error_failed_to_register_device)
  }

  private fun onFailure(errorId: Int) {
    view?.hideLoading()
    view?.showErrorState(textRes = errorId)
  }
  // endregion
}
