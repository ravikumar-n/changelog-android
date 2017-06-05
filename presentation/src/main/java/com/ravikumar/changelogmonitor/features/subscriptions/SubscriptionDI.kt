package com.ravikumar.changelogmonitor.features.subscriptions

import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.android.support.DaggerAppCompatActivity

@Module
interface SubscriptionBuilder {
  @ContributesAndroidInjector(
    modules = [
      SubscriptionModule::class
    ]
  )
  fun contributeSubscriptionActivity(): SubscriptionActivity
}

@Module
interface SubscriptionModule {
  @Binds fun providesAppCompatActivity(activity: SubscriptionActivity): DaggerAppCompatActivity
}
