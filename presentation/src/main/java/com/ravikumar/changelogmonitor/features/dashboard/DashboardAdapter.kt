package com.ravikumar.changelogmonitor.features.dashboard

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import dagger.android.support.DaggerFragment

class DashboardAdapter(
  fm: FragmentManager,
  private val fragments: List<DaggerFragment>
) : FragmentStatePagerAdapter(fm) {

  override fun getItem(position: Int): Fragment = fragments[position]

  override fun getCount(): Int = fragments.size
}
