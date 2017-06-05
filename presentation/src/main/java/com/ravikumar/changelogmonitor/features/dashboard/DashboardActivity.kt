package com.ravikumar.changelogmonitor.features.dashboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatDelegate
import android.view.MenuItem
import com.ravikumar.changelogmonitor.R
import com.ravikumar.changelogmonitor.features.feed.FeedFragment
import com.ravikumar.changelogmonitor.features.info.InfoFragment
import com.ravikumar.changelogmonitor.features.onboarding.OnboardingActivity
import com.ravikumar.changelogmonitor.features.userwatchlist.UserWatchlistFragment
import com.ravikumar.changelogmonitor.framework.extensions.bindInt
import com.ravikumar.changelogmonitor.ui.ViewPagerOnPageChangeListener
import com.ravikumar.data.preferences.FirebasePreferences
import com.ravikumar.data.preferences.IntPreference
import dagger.android.support.DaggerAppCompatActivity
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.activity_dashboard.bottomNavigationView
import kotlinx.android.synthetic.main.activity_dashboard.viewPager
import javax.inject.Inject

class DashboardActivity : DaggerAppCompatActivity(), DashboardContract.View {
  // region DI
  @Inject lateinit var firebasePreference: dagger.Lazy<FirebasePreferences>
  @Inject lateinit var presenter: DashboardPresenter<DashboardContract.View>
  @Inject lateinit var themePreference: dagger.Lazy<IntPreference>
  // endregion

  // region Variables
  private val lightTheme by bindInt(R.integer.theme_light)
  private val darkTheme by bindInt(R.integer.theme_dark)
  private val autoTheme by bindInt(R.integer.theme_daynight)
  private var prevMenuItem: MenuItem? = null
  // endregion

  // region Lifecycle
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    presenter.onAttach(this)
  }

  override fun onDestroy() {
    super.onDestroy()
    presenter.onDetach()
  }
  // endregion

  // region View
  override fun showLoading(textRes: Int) {
  }

  override fun hideLoading() {
  }

  override fun showEmptyState(
    drawableRes: Int,
    textRes: Int
  ) {
  }

  override fun showErrorState(
    drawableRes: Int,
    textRes: Int
  ) {
  }

  override fun onUserNotSignedIn() {
    startActivity(OnboardingActivity.start(this))
    finish()
  }

  override fun onUserSignedIn() {
    val systemTheme = AppCompatDelegate.getDefaultNightMode()
    val customTheme = themePreference.get()
      .get()
    if (customTheme == darkTheme && systemTheme != AppCompatDelegate.MODE_NIGHT_YES) {
      setNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    } else if (customTheme == lightTheme && systemTheme != AppCompatDelegate.MODE_NIGHT_NO) {
      setNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    } else if (customTheme == autoTheme && systemTheme != AppCompatDelegate.MODE_NIGHT_AUTO) {
      setNightMode(AppCompatDelegate.MODE_NIGHT_AUTO)
    } else {
      setContentView(R.layout.activity_dashboard)
      setupViewPager()
      updateDeviceInfoIfRequired()
    }
  }
  // endregion

  // region Private
  private fun setNightMode(@AppCompatDelegate.NightMode nightMode: Int) {
    AppCompatDelegate.setDefaultNightMode(nightMode)
    recreate()
  }

  private fun setupViewPager() {
    val fragments: List<DaggerFragment> = listOf(
      UserWatchlistFragment.newInstance(),
      FeedFragment.newInstance(),
      InfoFragment.newInstance()
    )

    val adapter = DashboardAdapter(supportFragmentManager, fragments)
    viewPager.offscreenPageLimit = fragments.size
    viewPager.adapter = adapter

    setViewPagerNavigationListener()
    setViewPagerChangeListener()
  }

  private fun setViewPagerNavigationListener() {
    bottomNavigationView.setOnNavigationItemSelectedListener { item ->
      val index = when (item.itemId) {
        R.id.action_watchlist -> 0
        R.id.action_feed -> 1
        R.id.action_info -> 2
        else -> null
      }
      index?.let { viewPager.currentItem = it }
      false
    }
  }

  private fun setViewPagerChangeListener() {
    viewPager.addOnPageChangeListener(object : ViewPagerOnPageChangeListener {
      override fun onPageSelected(position: Int) {
        if (prevMenuItem != null) {
          prevMenuItem?.isChecked = false
        } else {
          bottomNavigationView.menu.getItem(0)
            .isChecked = false
        }

        bottomNavigationView.menu.getItem(position)
          .isChecked = true
        prevMenuItem = bottomNavigationView.menu.getItem(position)
      }
    })
  }

  private fun updateDeviceInfoIfRequired() {
    val firebaseToken = firebasePreference.get()
      .getFirebaseToken()
    firebaseToken?.let(presenter::updateDeviceTokenIfRequired)
  }
  // endregion

  // region Static
  companion object {
    // region Intent
    fun start(context: Context): Intent {
      return Intent(context, DashboardActivity::class.java)
    }
    // endregion
  }
  // endregion

}
