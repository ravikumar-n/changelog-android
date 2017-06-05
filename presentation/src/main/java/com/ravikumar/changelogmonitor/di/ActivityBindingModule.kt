package com.ravikumar.changelogmonitor.di

import com.ravikumar.changelogmonitor.features.changelogdetail.ChangelogDetailBuilder
import com.ravikumar.changelogmonitor.features.dashboard.DashboardBuilder
import com.ravikumar.changelogmonitor.features.notification.ChangelogFirebaseInstanceIdService
import com.ravikumar.changelogmonitor.features.onboarding.OnboardingBuilder
import com.ravikumar.changelogmonitor.features.subscriptions.SubscriptionBuilder
import com.ravikumar.changelogmonitor.features.watchlist.WatchlistBuilder
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
(
  includes = [
    OnboardingBuilder::class,
    DashboardBuilder::class,
    WatchlistBuilder::class,
    ChangelogDetailBuilder::class,
    SubscriptionBuilder::class
  ]
)
abstract class ActivityBindingModule {
  @ContributesAndroidInjector
  internal abstract fun firebaseInstanceService(): ChangelogFirebaseInstanceIdService
}
