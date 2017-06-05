package com.ravikumar.changelogmonitor.base

import io.reactivex.disposables.CompositeDisposable

open class BasePresenter<V : MvpView> : MvpPresenter<V> {
  var view: V? = null
  val disposable: CompositeDisposable = CompositeDisposable()

  override fun onAttach(view: V) {
    this.view = view
  }

  override fun onDetach() {
    disposable.dispose()
    view = null
  }
}
