package com.ravikumar.changelogmonitor.base

import android.support.annotation.DrawableRes
import android.support.annotation.StringRes

interface MvpView {
  fun showLoading(@StringRes textRes: Int)

  fun hideLoading()

  fun showEmptyState(@DrawableRes drawableRes: Int = 0, @StringRes textRes: Int)

  fun showErrorState(@DrawableRes drawableRes: Int = 0, @StringRes textRes: Int)
}
