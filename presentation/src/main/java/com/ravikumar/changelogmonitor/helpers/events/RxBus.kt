package com.ravikumar.changelogmonitor.helpers.events

import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable

class RxBus {
  private val bus: Relay<Any> = PublishRelay.create<Any>()
    .toSerialized()

  fun send(event: Any) {
    bus.accept(event)
  }

  fun asFlowable(): Flowable<Any> {
    return bus.toFlowable(BackpressureStrategy.LATEST)
  }

  fun hasObservers(): Boolean {
    return bus.hasObservers()
  }
}
