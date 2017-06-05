package com.ravikumar.changelogmonitor.features.watchlist

import android.support.annotation.StringRes
import com.ravikumar.changelogmonitor.base.MvpPresenter
import com.ravikumar.changelogmonitor.base.MvpView
import com.ravikumar.entities.Meta
import com.ravikumar.entities.Repository
import java.util.UUID

interface WatchlistContract {
  interface Presenter<in V : View> : MvpPresenter<V> {
    fun fetchRepositories(offsetIndex: Int)

    fun saveWatchlist(itemsToWatch: List<UUID>)
  }

  interface View : MvpView {
    fun showRepositories(
      repositories: List<Repository>,
      meta: Meta
    )

    fun showProgressDialog(@StringRes textRes: Int)

    fun hideProgressDialog()

    fun onWatchlistSavedSuccessfully(
      @StringRes textRes: Int,
      watchlist: List<Repository>
    )

    fun onFailedToSaveWatchlist(error: Pair<Int, String?>)
  }
}
