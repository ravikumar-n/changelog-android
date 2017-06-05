package com.ravikumar.data.remote

import com.ravikumar.entities.AddWatchlist
import com.ravikumar.entities.ChangelogDetail
import com.ravikumar.entities.ChangelogResponse
import com.ravikumar.entities.ChangelogSubscription
import com.ravikumar.entities.DeviceInfo
import com.ravikumar.entities.DeviceRequest
import com.ravikumar.entities.Feedback
import com.ravikumar.entities.FeedbackRequest
import com.ravikumar.entities.NewUser
import com.ravikumar.entities.Repositories
import com.ravikumar.entities.SubscriptionRequest
import com.ravikumar.entities.SubscriptionResponse
import com.ravikumar.entities.User
import com.ravikumar.entities.UserResponse
import com.ravikumar.entities.Watchlist
import io.reactivex.Observable
import io.reactivex.Single
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteStore @Inject
constructor(private val api: ChangelogApiContract) : ChangelogApiContract {

  // region Repositories
  override fun getRepositories(index: Int): Observable<Repositories?> {
    return api.getRepositories(index)
  }
  // endregion

  // region User
  override fun createGuestUser(request: NewUser): Single<UserResponse?> {
    return api.createGuestUser(request)
  }

  override fun createUser(request: NewUser): Single<UserResponse?> {
    return api.createUser(request)
  }

  override fun connectUser(request: NewUser): Single<UserResponse?> {
    return api.connectUser(request)
  }
  // endregion

  // region Device
  override fun registerDevice(request: DeviceRequest): Single<DeviceInfo?> {
    return api.registerDevice(request)
  }

  override fun updateDevice(
    deviceId: UUID,
    request: DeviceRequest
  ): Single<DeviceInfo?> {
    return api.updateDevice(deviceId, request)
  }
  // endregion

  // region Watchlist
  override fun getWatchlist(userId: UUID): Single<Watchlist?> {
    return api.getWatchlist(userId)
  }

  override fun saveWatchlist(
    userId: UUID,
    items: AddWatchlist
  ): Single<Watchlist?> {
    return api.saveWatchlist(userId, items)
  }
  // endregion

  // region Subscriptions
  override fun getSubscription(userId: UUID): Single<ChangelogSubscription?> {
    return api.getSubscription(userId)
  }

  override fun postSubscription(
    userId: UUID,
    request: SubscriptionRequest
  ): Single<SubscriptionResponse?> {
    return api.postSubscription(userId, request)
  }

  override fun removeSubscription(userId: UUID): Single<User> {
    return api.removeSubscription(userId)
  }
  // endregion

  // region Changelog
  override fun getChangelogFeed(userId: UUID): Single<ChangelogResponse?> {
    return api.getChangelogFeed(userId)
  }

  override fun getChangelogDetail(repoId: UUID): Single<ChangelogDetail> {
    return api.getChangelogDetail(repoId)
  }
  // endregion

  // region Feedback
  override fun sendFeedback(request: FeedbackRequest): Single<Feedback?> {
    return api.sendFeedback(request)
  }
  // endregion
}
