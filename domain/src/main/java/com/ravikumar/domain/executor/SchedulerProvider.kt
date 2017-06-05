package com.ravikumar.domain.executor

import io.reactivex.Scheduler

interface SchedulerProvider {
  val uiScheduler: Scheduler
  val computationScheduler: Scheduler
  val ioScheduler: Scheduler
}
