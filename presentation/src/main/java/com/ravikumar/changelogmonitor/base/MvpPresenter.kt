package com.ravikumar.changelogmonitor.base

interface MvpPresenter<in V : MvpView> {
  fun onAttach(view: V)

  fun onDetach()
}
