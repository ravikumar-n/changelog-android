package com.ravikumar.changelogmonitor.features.dashboard

import com.ravikumar.changelogmonitor.features.feed.FeedFragment
import com.ravikumar.changelogmonitor.features.info.InfoFragment
import com.ravikumar.changelogmonitor.features.info.settings.ChangelogSettingsFragmentProvider
import com.ravikumar.changelogmonitor.features.userwatchlist.UserWatchlistFragment
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.android.support.DaggerAppCompatActivity

@Module
interface DashboardBuilder {
  @ContributesAndroidInjector(
    modules = [
      DashboardModule::class
    ]
  )
  fun contributeDashboardActivity(): DashboardActivity
}

@Module
interface DashboardModule {

  @Binds fun providesAppCompatActivity(activity: DashboardActivity): DaggerAppCompatActivity

  @ContributesAndroidInjector fun contributeUserWathclistFragment(): UserWatchlistFragment

  @ContributesAndroidInjector fun contributeFeedFragment(): FeedFragment

  @ContributesAndroidInjector(modules = [ChangelogSettingsFragmentProvider::class])
  fun contributeInfoFragment(): InfoFragment
}
