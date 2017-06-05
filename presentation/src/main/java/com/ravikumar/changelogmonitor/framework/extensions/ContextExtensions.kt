package com.ravikumar.changelogmonitor.framework.extensions

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import java.io.Serializable

val Context.layoutInflater: android.view.LayoutInflater
  get() = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as android.view.LayoutInflater

fun <T> Context.createIntent(
  clazz: Class<out T>,
  vararg params: Pair<String, Any?>
): Intent {
  val intent = Intent(this, clazz)
  if (params.isNotEmpty()) {
    params.forEach { (key, value) ->
      when (value) {
        is String -> intent.putExtra(key, value)
        is Serializable -> intent.putExtra(key, value)
        is Bundle -> intent.putExtra(key, value)
        is Parcelable -> intent.putExtra(key, value)
        is Array<*> -> {
          @Suppress("UNCHECKED_CAST")
          when {
            value.isArrayOf<Parcelable>() -> intent.putParcelableArrayListExtra(
              key,
              value as java.util.ArrayList<out Parcelable>
            )
            value.isArrayOf<CharSequence>() -> intent.putCharSequenceArrayListExtra(
              key,
              value as java.util.ArrayList<CharSequence>
            )
            value.isArrayOf<String>() -> intent.putStringArrayListExtra(
              key,
              value as java.util.ArrayList<String>
            )
            else -> throw IllegalArgumentException(
              "Unsupported bundle component (${value.javaClass})"
            )
          }
        }
      }
      return@forEach
    }
  }
  return intent
}
