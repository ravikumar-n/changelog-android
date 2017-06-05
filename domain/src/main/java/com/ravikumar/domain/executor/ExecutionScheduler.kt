package com.ravikumar.domain.executor

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single

interface ExecutionScheduler {
  val ui: Scheduler
  val highPriority: Scheduler
  val lowPriority: Scheduler
  val computation: Scheduler
  val io: Scheduler

  fun <T> highPriorityMaybe(): (Maybe<T>) -> Maybe<T>
  fun <T> lowPriorityMaybe(): (Maybe<T>) -> Maybe<T>

  fun <T> highPrioritySingle(): (Single<T>) -> Single<T>
  fun <T> lowPrioritySingle(): (Single<T>) -> Single<T>

  fun <T> highPriorityObservable(): (Observable<T>) -> Observable<T>
  fun <T> lowPriorityObservable(): (Observable<T>) -> Observable<T>

  fun <T> highPriorityFlowable(): (Flowable<T>) -> Flowable<T>
  fun <T> lowPriorityFlowable(): (Flowable<T>) -> Flowable<T>

  fun highPriorityCompletable(): (Completable) -> Completable
  fun lowPriorityCompletable(): (Completable) -> Completable
}
