package com.ravikumar.domain.executor

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThreadScheduler
@Inject constructor(private val threadProvider: SchedulerProvider) : ExecutionScheduler {

  companion object {
    private const val HIGH_PRIORITY_THREADS = 6
    private const val LOW_PRIORITY_THREADS = 2

    internal val highPriorityScheduler: Scheduler
    internal val lowPriorityScheduler: Scheduler

    init {
      highPriorityScheduler = Schedulers.from(
        JobExecutor(HIGH_PRIORITY_THREADS, "High-Priority-Pool")
      )
      lowPriorityScheduler = Schedulers.from(JobExecutor(LOW_PRIORITY_THREADS, "Low-Priority-Pool"))
    }
  }

  override val ui get() = threadProvider.uiScheduler
  override val highPriority get() = highPriorityScheduler
  override val lowPriority get() = lowPriorityScheduler
  override val computation get() = threadProvider.computationScheduler
  override val io get() = threadProvider.ioScheduler

  // region Maybe
  override fun <T> highPriorityMaybe() = { upstream: Maybe<T> ->
    upstream
      .subscribeOn(highPriority)
      .observeOn(ui)
  }

  override fun <T> lowPriorityMaybe() = { upstream: Maybe<T> ->
    upstream
      .subscribeOn(lowPriority)
      .observeOn(ui)
  }
  // endregion

  // region Single
  override fun <T> highPrioritySingle() = { upstream: Single<T> ->
    upstream
      .subscribeOn(highPriority)
      .observeOn(ui)
  }

  override fun <T> lowPrioritySingle() = { upstream: Single<T> ->
    upstream
      .subscribeOn(lowPriority)
      .observeOn(ui)
  }
  // endregion

  // region Observable
  override fun <T> highPriorityObservable() = { upstream: Observable<T> ->
    upstream
      .subscribeOn(highPriority)
      .observeOn(ui)
  }

  override fun <T> lowPriorityObservable() = { upstream: Observable<T> ->
    upstream
      .subscribeOn(lowPriority)
      .observeOn(ui)
  }
  // endregion

  // region Flowable
  override fun <T> highPriorityFlowable() = { upstream: Flowable<T> ->
    upstream.subscribeOn(highPriority)
      .observeOn(ui)
  }

  override fun <T> lowPriorityFlowable() = { upstream: Flowable<T> ->
    upstream.subscribeOn(lowPriority)
      .observeOn(ui)
  }
  // endregion

  // region Completable
  override fun highPriorityCompletable() = { upstream: Completable ->
    upstream.subscribeOn(highPriority)
      .observeOn(ui)
  }

  override fun lowPriorityCompletable() = { upstream: Completable ->
    upstream.subscribeOn(lowPriority)
      .observeOn(ui)
  }
  // endregion

}
