package com.ravikumar.changelogmonitor.features.info.settings

import com.ravikumar.changelogmonitor.base.MvpPresenter
import com.ravikumar.changelogmonitor.base.MvpView

interface ChangelogSettingsContract {
  interface Presenter<in V : View> : MvpPresenter<V> {
    fun sendFeedback(feedback: String)
  }

  interface View : MvpView {
    fun onFeedbackSent()

    fun onFailedToSendFeedback()
  }
}
