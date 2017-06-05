package com.ravikumar.changelogmonitor.helpers

import com.ravikumar.domain.executor.SchedulerProvider
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Singleton

@Singleton
class SchedulerThreads : SchedulerProvider {
  override val uiScheduler: Scheduler
    get() = AndroidSchedulers.mainThread()
  override val computationScheduler: Scheduler
    get() = Schedulers.io()
  override val ioScheduler: Scheduler
    get() = Schedulers.computation()
}
