package com.ravikumar.changelogmonitor.features.changelogdetail

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface ChangelogDetailBuilder {
  @ContributesAndroidInjector(
    modules = [
      ChangelogDetailModule::class
    ]
  )
  fun contributeChangelogDetailActivity(): ChangelogDetailActivity
}

@Module
interface ChangelogDetailModule {
  @ContributesAndroidInjector
  fun contributeChangelogDetailFragment(): ChangelogDetailFragment
}
