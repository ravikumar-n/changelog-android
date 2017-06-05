package com.ravikumar.changelogmonitor.base

import android.os.Bundle
import android.support.v4.app.Fragment
import com.ravikumar.changelogmonitor.R
import com.ravikumar.changelogmonitor.framework.extensions.inTransaction
import dagger.android.support.DaggerAppCompatActivity

abstract class BaseActivity : DaggerAppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_layout)
    addFragment(savedInstanceState)
  }

  private fun addFragment(savedInstanceState: Bundle?) =
    savedInstanceState ?: supportFragmentManager.inTransaction {
      add(R.id.fragmentContainer, fragment())
    }

  abstract fun fragment(): Fragment
}
