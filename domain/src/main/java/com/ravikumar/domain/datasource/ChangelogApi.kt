package com.ravikumar.domain.datasource

import com.ravikumar.entities.ChangelogDetail
import com.ravikumar.entities.ChangelogResponse
import com.ravikumar.entities.ChangelogSubscription
import com.ravikumar.entities.DeviceInfo
import com.ravikumar.entities.Feedback
import com.ravikumar.entities.NewUser
import com.ravikumar.entities.Repositories
import com.ravikumar.entities.Repository
import com.ravikumar.entities.SubscriptionRequest
import com.ravikumar.entities.SubscriptionResponse
import com.ravikumar.entities.User
import com.ravikumar.entities.UserResponse
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import java.util.UUID

interface ChangelogApi {
  // region User
  fun createGuestUser(newUser: NewUser): Single<UserResponse?>

  fun createUser(newUser: NewUser): Single<UserResponse?>

  fun connectUser(newUser: NewUser): Single<UserResponse?>

  fun getUser(): Single<User?>
  // endregion

  // region Device
  fun registerDevice(deviceInfo: DeviceInfo): Single<DeviceInfo?>

  fun updateDeviceToken(firebaseToken: String): Maybe<DeviceInfo?>
  // endregion

  // region Watchlist
  fun repositories(index: Int): Observable<Repositories?>

  fun getRepository(uuid: UUID): Maybe<Repository>

  fun getWatchlist(): Flowable<List<Repository>?>

  fun saveWatchlist(itemsToWatch: List<UUID>): Single<List<Repository>?>
  // endregion

  // region Subscriptions
  fun getSubscription(userId: UUID): Single<ChangelogSubscription?>

  fun postSubscription(
    userId: UUID,
    request: SubscriptionRequest
  ): Single<SubscriptionResponse?>

  fun removeSubscription(): Single<User?>
  // endregion

  // region Changelog
  fun getChangelogFeed(userId: UUID): Flowable<ChangelogResponse?>

  fun getShortChangelog(repoId: UUID): Maybe<ChangelogDetail>

  fun getChangelogDetail(repoId: UUID): Flowable<ChangelogDetail>
  // endregion

  // region Feedback
  fun sendFeedback(feedback: String): Single<Feedback?>
  // endregion

  // region Logout
  fun signout(): Completable
  // endregion
}
