package com.ravikumar.changelogmonitor.ui

import android.support.v4.view.ViewPager

interface ViewPagerOnPageChangeListener : ViewPager.OnPageChangeListener {
  override fun onPageScrolled(
    position: Int,
    positionOffset: Float,
    positionOffsetPixels: Int
  ) {
  }

  override fun onPageSelected(position: Int) {
  }

  override fun onPageScrollStateChanged(state: Int) {
  }
}
