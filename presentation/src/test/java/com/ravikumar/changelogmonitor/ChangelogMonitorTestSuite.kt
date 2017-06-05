package com.ravikumar.changelogmonitor

import com.ravikumar.changelogmonitor.base.BasePresenterTest
import com.ravikumar.changelogmonitor.features.dashboard.DashboardPresenterTest
import com.ravikumar.changelogmonitor.features.feed.FeedPresenterTest
import com.ravikumar.changelogmonitor.features.info.InfoPresenterTest
import com.ravikumar.changelogmonitor.features.onboarding.OnboardingPresenterTest
import com.ravikumar.changelogmonitor.features.watchlist.WatchlistPresenterTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
  BasePresenterTest::class,
  OnboardingPresenterTest::class,
  DashboardPresenterTest::class,
  WatchlistPresenterTest::class,
  FeedPresenterTest::class,
  InfoPresenterTest::class
)
class ChangelogMonitorTestSuite
