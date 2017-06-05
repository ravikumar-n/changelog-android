package com.ravikumar.data.preferences

import android.content.SharedPreferences

class BooleanPreference
constructor(
  private val sharedPreferences: SharedPreferences,
  private val key: String,
  private val defaultValue: Boolean = false
) {

  val isSet: Boolean
    get() = sharedPreferences.contains(key)

  fun get(): Boolean? {
    return sharedPreferences.getBoolean(key, defaultValue)
  }

  fun set(value: Boolean) {
    sharedPreferences.edit()
      .putBoolean(key, value)
      .apply()
  }

  fun clear() {
    sharedPreferences.edit()
      .remove(key)
      .apply()
  }
}
