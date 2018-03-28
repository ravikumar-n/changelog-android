package com.ravikumar.changelogmonitor.features.subscriptions

import com.android.billingclient.api.Purchase
import com.ravikumar.changelogmonitor.R
import com.ravikumar.changelogmonitor.base.BasePresenter
import com.ravikumar.changelogmonitor.framework.extensions.errorMessage
import com.ravikumar.domain.usecases.subscriptions.AddSubscriptionUseCase
import com.ravikumar.domain.usecases.subscriptions.GetSubscriptionUseCase
import com.ravikumar.domain.usecases.subscriptions.RemoveSubscriptionUseCase
import com.ravikumar.domain.usecases.user.ConnectUserUseCase
import com.ravikumar.domain.usecases.user.GetUserUseCase
import com.ravikumar.entities.ChangelogSubscription
import com.ravikumar.entities.SubscriptionRequest
import com.ravikumar.entities.User
import com.ravikumar.entities.UserResponse
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

class SubscriptionsPresenter<V : SubscriptionContract.View>
@Inject constructor(
  private val getUserUseCase: GetUserUseCase,
  private val getSubscriptionUseCase: GetSubscriptionUseCase,
  private val addSubscriptionUseCase: AddSubscriptionUseCase,
  private val connectUserUseCase: ConnectUserUseCase,
  private val removeSubscriptionUseCase: RemoveSubscriptionUseCase
) : BasePresenter<V>(), SubscriptionContract.Presenter<V> {
  // region Variables
  private var user: User? = null
  // endregion

  // region Init
  init {
    disposable.addAll(
      getUserUseCase.disposables,
      getSubscriptionUseCase.disposables,
      addSubscriptionUseCase.disposables,
      connectUserUseCase.disposables,
      removeSubscriptionUseCase.disposables
    )
  }
  // endregion

  // region Presenter
  override fun onAttach(view: V) {
    super.onAttach(view)
    getUserUseCase.execute({ user: User? ->
      this.user = user
    }, { throwable ->
      Timber.e(throwable, "Failed to get the user info")
    })
  }

  override fun checkAndRegisterPurchase(purchase: Purchase) {
    user?.let {
      if (it.emailId == null) {
        // User is guest user, Guest user is not allowed to buy subscription.
        view?.showFreePlan()
        return
      }

      if (it.subscriptionId == null) {
        // User is registering subscription for first time
        view?.showLoading(R.string.progress_register_subscription)
        registerSubscription(it.id, purchase)
      } else {
        // Get the existing subscription info
        getSubscriptionForUser(it)
      }
    }
  }

  override fun isGuestUser() = user?.emailId == null

  override fun connectUser(
    idToken: String,
    userName: String,
    email: String
  ) {
    view?.onGoogleSignIn(userName)

    connectUserUseCase.execute({ userResponse: UserResponse? ->
      if (userResponse?.user != null) {
        user = userResponse.user
        view?.onGoogleSignInSuccess(user!!)
      } else {
        view?.onGoogleSignInFailed()
      }
    }, { throwable ->
      Timber.e(throwable, "Failed to connect the google account with guest account ")
      view?.onGoogleSignInFailed()
    }, ConnectUserUseCase.Params(idToken, user?.copy(emailId = email)!!))
  }

  override fun removeSubscriptionsIfAny() {
    user?.subscriptionId?.let {
      removeSubscriptionUseCase.execute({
        view?.onSubscriptionRemoved()
      }, { throwable ->
        Timber.e(throwable, "Failed to remove the subscription")
      })
    }
  }
  // endregion

  // region Private
  private fun getSubscriptionForUser(user: User) {
    getSubscriptionUseCase.execute({
      if (it == null) {
        view?.showFreePlan()
      } else {
        view?.showSubscription(it)
      }
    }, { throwable ->
      Timber.e(throwable, "Failed to get the subscription for the user")
      view?.showErrorState(textRes = R.string.error_fetch_subscription)
    }, GetSubscriptionUseCase.Params(user.id))
  }

  private fun registerSubscription(
    userId: UUID,
    purchase: Purchase
  ) {
    val subscription = ChangelogSubscription(
      orderId = purchase.orderId,
      packageName = purchase.packageName,
      productId = purchase.sku,
      purchaseTime = purchase.purchaseTime,
      purchaseToken = purchase.purchaseToken,
      autoRenewing = purchase.isAutoRenewing
    )


    addSubscriptionUseCase.execute({
      view?.hideLoading()
      it?.let {
        view?.onSubscriptionPurchased(it)
        view?.showSubscription(it.subscription)
      }
    }, { throwable ->
      Timber.e(throwable, "Failed to register the subscription")
      view?.hideLoading()
      view?.onFailedToRegisterSubscription(
        Pair(R.string.error_register_subscription, throwable.errorMessage())
      )
    }, AddSubscriptionUseCase.Params(userId, SubscriptionRequest(subscription)))
  }
// endregion
}
