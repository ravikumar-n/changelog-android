package com.ravikumar.changelogmonitor.features.onboarding

import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.android.support.DaggerAppCompatActivity

@Module
interface OnboardingBuilder {
  @ContributesAndroidInjector(
    modules = [
      OnboardingModule::class
    ]
  )
  fun contributeOnboardingActivity(): OnboardingActivity
}

@Module
interface OnboardingModule {
  @Binds fun providesAppCompatActivity(activity: OnboardingActivity): DaggerAppCompatActivity
}
