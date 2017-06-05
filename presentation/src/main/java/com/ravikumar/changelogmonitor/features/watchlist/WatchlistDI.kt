package com.ravikumar.changelogmonitor.features.watchlist

import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector

@Module
interface WatchlistBuilder {
  @ContributesAndroidInjector(modules = [WatchlistModule::class])
  fun contributeWatchlistActivity(): WatchlistActivity
}

@Module
class WatchlistModule {
  @Provides
  fun provideAdapter(): WatchlistAdapter {
    return WatchlistAdapter()
  }
}
