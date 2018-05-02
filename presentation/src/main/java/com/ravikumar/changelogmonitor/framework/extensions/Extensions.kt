package com.ravikumar.changelogmonitor.framework.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.annotation.ColorRes
import android.support.annotation.IntegerRes
import android.support.annotation.LayoutRes
import android.support.customtabs.CustomTabsIntent
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import com.ravikumar.changelogmonitor.APP_PLAYSTORE_URI
import com.ravikumar.changelogmonitor.ChangelogApplication
import com.ravikumar.changelogmonitor.R
import com.ravikumar.changelogmonitor.framework.customtabs.CustomTabActivityHelper
import com.ravikumar.entities.ApiError

inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) =
  beginTransaction().func().commit()

var TextView.textResource: Int
  get() = throw IllegalAccessException()
  set(value) = setText(value)

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
  return context.layoutInflater.inflate(layoutRes, this, attachToRoot)
}

fun View.getString(resId: Int): String = resources.getString(resId)

fun Context.getInt(resId: Int): Int = resources.getInteger(resId)

fun Fragment.bindInt(@IntegerRes res: Int): Lazy<Int?> = lazy(LazyThreadSafetyMode.NONE) {
  context?.getInt(res)
}

fun Context.bindInt(@IntegerRes res: Int): Lazy<Int> =
  lazy(LazyThreadSafetyMode.NONE) { getInt(res) }

fun Context.bindColor(@ColorRes color: Int): Lazy<Int> = lazy(LazyThreadSafetyMode.NONE) {
  ContextCompat.getColor(this, color)
}

/**
 * Extract the custom error message from the throwable.
 *
 * @return error message from API else null
 */
fun Throwable.errorMessage(): String? = (ChangelogApplication.gson?.fromJson(
  (this as? HttpException)?.response()?.errorBody()?.string(),
  ApiError::class.java
))?.message

fun Context.statusBarHeight(): Int {
  var result = 0
  val resourceId = this.resources.getIdentifier("status_bar_height", "dimen", "android")
  if (resourceId > 0) {
    result = resources.getDimensionPixelSize(resourceId)
  }
  return result
}
