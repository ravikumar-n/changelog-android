package com.ravikumar.changelogmonitor.features.changelogdetail

import android.support.annotation.StringRes
import com.ravikumar.changelogmonitor.base.MvpPresenter
import com.ravikumar.changelogmonitor.base.MvpView
import com.ravikumar.entities.Repository
import java.util.UUID

interface ChangelogDetailContract {
  interface Presenter<in V : View> : MvpPresenter<V> {
    fun fetchRepository(repoId: UUID)

    fun fetchChangelogDetail(
      repoId: UUID,
      repoTitle: String?
    )
  }

  interface View : MvpView {
    fun showRepository(repository: Repository)

    fun showChangelog(changelog: String)

    fun showUserHint(@StringRes res: Int)
  }
}
