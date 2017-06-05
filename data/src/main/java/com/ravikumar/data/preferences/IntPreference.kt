package com.ravikumar.data.preferences

import android.content.SharedPreferences

class IntPreference
constructor(
  private val sharedPreferences: SharedPreferences,
  private val key: String,
  private val defaultValue: Int = 0
) {

  val isSet: Boolean
    get() = sharedPreferences.contains(key)

  fun get(): Int {
    return sharedPreferences.getInt(key, defaultValue)
  }

  fun set(value: Int) {
    sharedPreferences.edit()
      .putInt(key, value)
      .apply()
  }

  fun clear() {
    sharedPreferences.edit()
      .remove(key)
      .apply()
  }
}
