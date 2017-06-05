package com.ravikumar.changelogmonitor.features.userwatchlist

import com.ravikumar.changelogmonitor.base.MvpPresenter
import com.ravikumar.changelogmonitor.base.MvpView
import com.ravikumar.entities.Repository

interface UserWatchlistContract {
  interface Presenter<in V : View> : MvpPresenter<V> {
    fun fetchWatchlist()
  }

  interface View : MvpView {
    fun showWatchlist(items: List<Repository>)
  }
}
