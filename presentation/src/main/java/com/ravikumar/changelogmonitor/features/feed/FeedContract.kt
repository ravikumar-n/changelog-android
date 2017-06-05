package com.ravikumar.changelogmonitor.features.feed

import com.ravikumar.changelogmonitor.base.MvpPresenter
import com.ravikumar.changelogmonitor.base.MvpView
import com.ravikumar.entities.Changelog
import com.ravikumar.entities.Repository

interface FeedContract {
  interface Presenter<in V : View> : MvpPresenter<V> {
    fun fetchFeed()
  }

  interface View : MvpView {
    fun showFeed(
      changelogs: List<Changelog>,
      repositories: List<Repository>
    )
  }
}
