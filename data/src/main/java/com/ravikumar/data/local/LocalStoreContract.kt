package com.ravikumar.data.local

import com.ravikumar.entities.Changelog
import com.ravikumar.entities.ChangelogDetail
import com.ravikumar.entities.ChangelogSubscription
import com.ravikumar.entities.DeviceInfo
import com.ravikumar.entities.Repository
import com.ravikumar.entities.User
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import java.util.UUID

interface LocalStoreContract {
  // region User
  fun saveUser(user: User): Completable

  fun getUser(): Single<User?>
  // endregion

  // region Device
  fun saveDevice(device: DeviceInfo): Completable

  fun getDevice(): Single<DeviceInfo?>
  // endregion

  // region UserWatchlist
  fun getAllUserWatchlist(): Single<List<Repository>>

  fun getRepository(uuid: UUID): Maybe<Repository>

  fun getUserWatchlist(ids: List<UUID>): Single<List<Repository>>

  fun saveUserWatchlist(repositories: List<Repository>): Completable
  //endregion

  // region Subscription
  fun saveSubscription(subscription: ChangelogSubscription): Completable

  fun getSubscription(): Maybe<ChangelogSubscription>
  // endregion

  // region Changelog
  fun getChangelogs(): Single<List<Changelog>>

  fun getChangelog(repoId: UUID): Maybe<Changelog>

  fun saveChangelogs(changelogs: List<Changelog>): Completable

  fun getChangelogDetail(repoId: UUID): Single<ChangelogDetail?>

  fun saveChangelogDetail(detail: ChangelogDetail): Completable
  //endregion

  // region Logout
  fun signout(): Completable
  // endregion
}
