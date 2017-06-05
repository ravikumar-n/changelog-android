package com.ravikumar.changelogmonitor.features.info

import com.ravikumar.changelogmonitor.base.BasePresenter
import com.ravikumar.domain.usecases.SignOutUseCase
import com.ravikumar.domain.usecases.subscriptions.GetSubscriptionUseCase
import com.ravikumar.domain.usecases.user.GetUserUseCase
import com.ravikumar.entities.ChangelogSubscription
import com.ravikumar.entities.User
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

class InfoPresenter<V : InfoContract.View>
@Inject constructor(
  private val getUserUseCase: GetUserUseCase,
  private val getSubscriptionUseCase: GetSubscriptionUseCase,
  private val signOutUseCase: SignOutUseCase
) : BasePresenter<V>(), InfoContract.Presenter<V> {
  // region Init
  init {
    disposable.addAll(
      getUserUseCase.disposables,
      getSubscriptionUseCase.disposables,
      signOutUseCase.disposables
    )
  }
  // endregion

  // region Presenter
  override fun onAttach(view: V) {
    super.onAttach(view)
    fetchUserInfo()
  }

  override fun fetchUserInfo() {
    getUserUseCase.execute({ user: User? ->
      user?.let {
        view?.showUserInfo(it)
        it.subscriptionId?.let(this::fetchSubscriptionInfo)
      }
    }, { throwable ->
      Timber.e(throwable, "Failed to fetch user info")
    })
  }

  override fun fetchSubscriptionInfo(userId: UUID) {
    getSubscriptionUseCase.execute({ subscription: ChangelogSubscription? ->
      if (subscription == null) {
        view?.showFreePlan()
      } else {
        view?.showSubscription(subscription)
      }
    }, { throwable ->
      Timber.e(throwable, "Failed to subscription info")
    }, GetSubscriptionUseCase.Params(userId))
  }

  override fun signout() {
    signOutUseCase.execute(onComplete = {
      view?.onUserSignedOut()
    })
  }
  // endregion
}
