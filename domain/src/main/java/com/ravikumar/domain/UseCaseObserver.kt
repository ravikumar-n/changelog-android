package com.ravikumar.domain

import io.reactivex.observers.DisposableMaybeObserver
import io.reactivex.observers.DisposableObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.subscribers.DisposableSubscriber

class UseCaseObserver {
  open class RxMaybe<T> : DisposableMaybeObserver<T>() {
    override fun onSuccess(value: T) {}
    override fun onError(e: Throwable) {}
    override fun onComplete() {}
  }

  open class RxSingle<T> : DisposableSingleObserver<T>() {
    override fun onSuccess(value: T) {}
    override fun onError(e: Throwable) {}
  }

  open class RxObservable<T> : DisposableObserver<T>() {
    override fun onComplete() {}
    override fun onNext(value: T) {}
    override fun onError(e: Throwable) {}
  }

  open class RxFlowable<T> : DisposableSubscriber<T>() {
    override fun onComplete() {}
    override fun onNext(value: T) {}
    override fun onError(e: Throwable) {}
  }
}
