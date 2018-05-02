package com.ravikumar.changelogmonitor.framework.extensions

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.support.customtabs.CustomTabsIntent
import com.ravikumar.changelogmonitor.APP_PLAYSTORE_URI
import com.ravikumar.changelogmonitor.R
import com.ravikumar.changelogmonitor.framework.customtabs.CustomTabActivityHelper

fun String.getHumanReadableTags(): String {
  return this
    .split(";")
    .filter { it ->
      it.trim()
        .isNotEmpty()
    }
    .joinToString(separator = " ") { tag: String ->
      "#${tag.toUpperCase()}"
    }
    .trim()
}

fun String.openInCustomTab(activity: Activity) {
  val customTabsIntent = CustomTabsIntent.Builder()
    .build()
  customTabsIntent.intent.putExtra(CustomTabsIntent.EXTRA_TOOLBAR_COLOR, R.color.primary)
  customTabsIntent.intent.putExtra(
    Intent.EXTRA_REFERRER,
    Uri.parse(APP_PLAYSTORE_URI)
  )

  CustomTabActivityHelper.openCustomTab(activity, customTabsIntent, Uri.parse(this))
}
