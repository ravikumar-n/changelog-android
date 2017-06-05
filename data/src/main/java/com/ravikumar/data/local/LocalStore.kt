package com.ravikumar.data.local

import com.ravikumar.data.local.dao.ChangelogDao
import com.ravikumar.data.local.dao.ChangelogDetailDao
import com.ravikumar.data.local.dao.DeviceDao
import com.ravikumar.data.local.dao.SubscriptionDao
import com.ravikumar.data.local.dao.UserDao
import com.ravikumar.data.local.dao.UserWatchlistDao
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
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalStore @Inject
constructor(
  private val userDao: UserDao,
  private val deviceDao: DeviceDao,
  private val watchlistDao: UserWatchlistDao,
  private val subscriptionDao: SubscriptionDao,
  private val changelogDao: ChangelogDao,
  private val changelogDetailDao: ChangelogDetailDao
) : LocalStoreContract {

  // region User
  override fun getUser(): Single<User?> {
    return userDao.getUser()
  }

  override fun saveUser(user: User): Completable {
    return Completable.fromCallable {
      userDao.deleteAllUsers()
      userDao.saveUser(user)
    }
  }
  // endregion

  // region Device
  override fun getDevice(): Single<DeviceInfo?> {
    return deviceDao.getDevice()
  }

  override fun saveDevice(device: DeviceInfo): Completable {
    return Completable.fromCallable {
      deviceDao.deleteAllDevices()
      deviceDao.saveDevice(device)
    }
  }
  // endregion

  // region UserWatchlist
  override fun getAllUserWatchlist(): Single<List<Repository>> {
    return watchlistDao.getAllUserWatchlist()
  }

  override fun getRepository(uuid: UUID): Maybe<Repository> {
    return watchlistDao.getRepository(uuid)
  }

  override fun getUserWatchlist(ids: List<UUID>): Single<List<Repository>> {
    return watchlistDao.getUserWatchlist(ids)
  }

  override fun saveUserWatchlist(repositories: List<Repository>): Completable {
    return Completable.fromCallable {
      watchlistDao.deleteAllWatchlist()
      watchlistDao.saveAllUserWatchlist(*repositories.toTypedArray())
    }
  }
  // endregion

  // region Subscriptions
  override fun saveSubscription(subscription: ChangelogSubscription): Completable {
    return Completable.fromCallable {
      subscriptionDao.deleteAllSubscriptions()
      subscriptionDao.saveSubscription(subscription)
    }
  }

  override fun getSubscription(): Maybe<ChangelogSubscription> {
    return subscriptionDao.getSubscription()
  }
  // endregion

  // region Changelogs
  override fun getChangelogs(): Single<List<Changelog>> {
    return changelogDao.getUserChangelog()
  }

  override fun saveChangelogs(changelogs: List<Changelog>): Completable {
    return Completable.fromCallable {
      changelogDao.deleteAllChangelogs()
      changelogDao.saveAllChangelog(*changelogs.toTypedArray())
    }
  }

  override fun getChangelog(repoId: UUID): Maybe<Changelog> {
    return changelogDao.getChangelog(repoId)
  }

  override fun getChangelogDetail(repoId: UUID): Single<ChangelogDetail?> {
    return changelogDetailDao.getChangelogDetail(repoId)
  }

  override fun saveChangelogDetail(detail: ChangelogDetail): Completable {
    return Completable.fromCallable {
      changelogDetailDao.saveChangelogDetail(detail)
    }
  }
  // endregion

  // region Logout
  override fun signout(): Completable {
    return Completable.fromCallable {
      watchlistDao.deleteAllWatchlist()
      changelogDao.deleteAllChangelogs()
      changelogDetailDao.deleteAllDetails()
      subscriptionDao.deleteAllSubscriptions()
      userDao.deleteAllUsers()
      deviceDao.deleteAllDevices()
    }
  }
  // endregion
}
