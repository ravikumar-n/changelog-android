package com.ravikumar.data

import com.ravikumar.data.local.LocalStore
import com.ravikumar.data.remote.RemoteStore
import com.ravikumar.domain.datasource.ChangelogApi
import com.ravikumar.domain.executor.ThreadScheduler
import com.ravikumar.entities.AddWatchlist
import com.ravikumar.entities.Changelog
import com.ravikumar.entities.ChangelogDetail
import com.ravikumar.entities.ChangelogResponse
import com.ravikumar.entities.ChangelogSubscription
import com.ravikumar.entities.DeviceInfo
import com.ravikumar.entities.DeviceRequest
import com.ravikumar.entities.Feedback
import com.ravikumar.entities.FeedbackRequest
import com.ravikumar.entities.NewUser
import com.ravikumar.entities.Repositories
import com.ravikumar.entities.Repository
import com.ravikumar.entities.SubscriptionRequest
import com.ravikumar.entities.SubscriptionResponse
import com.ravikumar.entities.User
import com.ravikumar.entities.UserResponse
import com.ravikumar.entities.Watchlist
import io.reactivex.BackpressureStrategy.LATEST
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import timber.log.Timber
import java.io.IOException
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChangelogDataManager @Inject
constructor(
  private val localStore: LocalStore,
  private val remoteStore: RemoteStore,
  private val scheduler: ThreadScheduler
) : ChangelogApi {

  // region Repositories
  override fun repositories(index: Int): Observable<Repositories?> =
    remoteStore.getRepositories(index)

  override fun getRepository(uuid: UUID): Maybe<Repository> {
    return localStore.getRepository(uuid)
  }
  // endregion

  // region User
  override fun getUser(): Single<User?> = localStore.getUser()

  override fun createGuestUser(newUser: NewUser): Single<UserResponse?> {
    return remoteStore.createGuestUser(newUser)
      .doOnSuccess {
        it?.user?.let(localStore::saveUser)
          ?.subscribe()
      }
  }

  override fun createUser(newUser: NewUser): Single<UserResponse?> {
    return remoteStore.createUser(newUser)
      .doOnSuccess { response: UserResponse? ->
        Timber.d("createUser::doOnSuccess : %s", response)
        response?.user?.let(localStore::saveUser)
          ?.blockingAwait()
      }
  }

  override fun connectUser(newUser: NewUser): Single<UserResponse?> {
    return remoteStore.createUser(newUser)
      .doOnSuccess { response: UserResponse? ->
        Timber.d("connectUser::doOnSuccess : %s", response)
        response?.user?.let(localStore::saveUser)
          ?.blockingAwait()
      }
  }
  // endregion

  // region Device
  override fun registerDevice(deviceInfo: DeviceInfo): Single<DeviceInfo?> {
    return remoteStore.registerDevice(DeviceRequest(deviceInfo))
      .doOnSuccess {
        it?.let(localStore::saveDevice)
          ?.blockingAwait()
      }
  }

  /**
   *  Update device only existing device token and firebase toke doesn't match.
   */
  override fun updateDeviceToken(firebaseToken: String): Maybe<DeviceInfo?> {
    val deviceInfo: Maybe<DeviceInfo?> = localStore.getDevice()
      .map { deviceInfo: DeviceInfo ->
        if (deviceInfo.token != firebaseToken) {
          return@map deviceInfo.copy(token = firebaseToken)
        }
        return@map deviceInfo.copy(token = DEVICE_TOKEN_NOT_UPDATED)
      }
      .filter { deviceInfo -> deviceInfo.token != DEVICE_TOKEN_NOT_UPDATED }
      .subscribeOn(scheduler.computation)

    return deviceInfo.flatMap { it ->
      remoteStore
        .updateDevice(it.id, DeviceRequest(it))
        .doOnSuccess {
          it?.let(localStore::saveDevice)
            ?.blockingAwait()
        }
        .toMaybe()
    }
  }
  // endregion

  // region Watchlist
  override fun getWatchlist(): Flowable<List<Repository>?> {
    val remoteSource: Single<List<Repository>> =
      getUser()
        .map(User::id)
        .flatMap {
          remoteStore.getWatchlist(it)
            .map(Watchlist::items)
            .onErrorResumeNext { throwable ->
              if (throwable is IOException) {
                return@onErrorResumeNext localStore.getAllUserWatchlist()
              }
              return@onErrorResumeNext Single.error(throwable)
            }
            .subscribeOn(scheduler.io)
        }

    return localStore.getAllUserWatchlist()
      .flatMapObservable { listFromLocal: List<Repository> ->
        remoteSource
          .observeOn(scheduler.computation)
          .toObservable()
          .filter { apiWatchList: List<Repository> ->
            apiWatchList != listFromLocal
          }
          .flatMapSingle { apiWatchList ->
            localStore.saveUserWatchlist(apiWatchList)
              .andThen(Single.just(apiWatchList))
          }
          .startWith(listFromLocal)
      }
      .toFlowable(LATEST)
  }

  override fun saveWatchlist(itemsToWatch: List<UUID>): Single<List<Repository>?> {
    return getUser()
      .map(User::id)
      .flatMap {
        remoteStore.saveWatchlist(it, AddWatchlist(itemsToWatch))
          .map(Watchlist::items)
          .doAfterSuccess { response: List<Repository>? ->
            response?.let {
              localStore.saveUserWatchlist(response)
                .subscribeOn(scheduler.io)
                .subscribe()
            }
          }
      }
  }
  // endregion

  // region Subscriptions
  override fun getSubscription(userId: UUID): Single<ChangelogSubscription?> {
    val remoteSource: Single<ChangelogSubscription?> =
      remoteStore
        .getSubscription(userId)
        .subscribeOn(scheduler.io)

    return localStore.getSubscription()
      .doOnSuccess { localSubscription: ChangelogSubscription? ->
        Timber.d("getSubscription::doOnSuccess : %s", localSubscription)
        remoteSource
          .observeOn(scheduler.computation)
          .filter { apiSubscription: ChangelogSubscription -> apiSubscription != localSubscription }
          .flatMapSingle { apiSubscription ->
            localStore.saveSubscription(apiSubscription)
              .andThen(Single.just(apiSubscription))
          }
      }
      .toSingle()
      .onErrorReturn { throwable: Throwable? ->
        Timber.e(throwable, "getSubscription::onErrorReturn")
        return@onErrorReturn remoteSource
          .doAfterSuccess { apiSubscription ->
            apiSubscription?.let {
              localStore.saveSubscription(apiSubscription)
                .blockingAwait()
            }
          }
          .blockingGet()
      }
  }

  override fun postSubscription(
    userId: UUID,
    request: SubscriptionRequest
  ): Single<SubscriptionResponse?> {
    return remoteStore.postSubscription(userId, request)
      .flatMap { response: SubscriptionResponse? ->
        response?.let {
          localStore.saveUser(it.user)
            .andThen(localStore.saveSubscription(it.subscription))
            .andThen(Single.just(it))
        }
      }
  }

  override fun removeSubscription(): Single<User?> {
    return getUser()
      .map(User::id)
      .flatMap(remoteStore::removeSubscription)
      .doOnSuccess {
        it?.let(localStore::saveUser)
          ?.subscribe()
      }
  }
  // endregion

  // region Changelog
  override fun getChangelogFeed(userId: UUID): Flowable<ChangelogResponse?> {
    return getLocalChangelogResponse()
      .flatMapObservable { cache: ChangelogResponse ->
        getChangelogResponseRemote(userId)
          .observeOn(scheduler.computation)
          .toObservable()
          .filter { apiResponse: ChangelogResponse ->
            cache != apiResponse
          }
          .flatMapSingle { apiResponse ->
            localStore.saveChangelogs(apiResponse.changelogs)
              .andThen(Single.just(apiResponse))
          }
          .startWith(cache)
      }
      .toFlowable(LATEST)
  }

  override fun getShortChangelog(repoId: UUID): Maybe<ChangelogDetail> {
    return localStore
      .getChangelog(repoId)
      .map { it -> ChangelogDetail(repoId, it.latestChangelog) }
      .onErrorComplete()
  }

  override fun getChangelogDetail(repoId: UUID): Flowable<ChangelogDetail> {
    val remoteSource: Single<ChangelogDetail> =
      remoteStore.getChangelogDetail(repoId)
        .onErrorResumeNext { throwable ->
          if (throwable is IOException) {
            return@onErrorResumeNext localStore.getChangelogDetail(repoId)
          }
          return@onErrorResumeNext Single.error(throwable)
        }
        .subscribeOn(scheduler.io)

    return localStore.getChangelogDetail(repoId)
      .onErrorReturnItem(ChangelogDetail())
      .flatMapObservable { cache: ChangelogDetail? ->
        remoteSource
          .observeOn(scheduler.computation)
          .toObservable()
          .filter { apiResponse: ChangelogDetail ->
            cache?.changelog != apiResponse.changelog
          }
          .flatMapSingle { apiResponse ->
            localStore.saveChangelogDetail(apiResponse)
              .andThen(Single.just(apiResponse))
          }
          .startWith(cache)
      }
      .toFlowable(LATEST)
  }
  // endregion

  // region Feedback
  override fun sendFeedback(feedback: String): Single<Feedback?> {
    return Single.zip(getUser(), localStore.getDevice(),
      BiFunction { t1: User?, t2: DeviceInfo? -> Pair(t1?.id, t2?.id) })
      .filter { it -> it.first != null && it.second != null }
      .map { it ->
        FeedbackRequest(
          Feedback(
            userId = it.first!!,
            deviceId = it.second!!,
            feedback = feedback
          )
        )
      }
      .flatMapSingle { remoteStore.sendFeedback(it) }
  }
  // endregion

  // region Logout
  override fun signout(): Completable = localStore.signout()
  // endregion

  // region Private
  private fun getLocalChangelogResponse(): Single<ChangelogResponse?> {
    return localStore.getChangelogs()
      .flatMap { changelogs: List<Changelog> ->
        val repoIds: List<UUID> = changelogs.map(Changelog::repoId)
        localStore
          .getUserWatchlist(repoIds)
          .map { repositories: List<Repository> ->
            ChangelogResponse(changelogs, repositories)
          }
      }
  }

  private fun getChangelogResponseRemote(userId: UUID): Single<ChangelogResponse?> {
    return remoteStore.getChangelogFeed(userId)
      .onErrorResumeNext { throwable ->
        if (throwable is IOException) {
          return@onErrorResumeNext getLocalChangelogResponse()
        }
        return@onErrorResumeNext Single.error(throwable)
      }
      .subscribeOn(scheduler.io)
  }
  // endregion

  // region Static
  companion object {
    // region Constants
    const val DEVICE_TOKEN_NOT_UPDATED = ""
    // endregion
  }
  // endregion
}
