package com.ravikumar.changelogmonitor.framework.utils

import android.content.Context
import android.os.Build
import android.provider.Settings
import android.support.annotation.NonNull

object DeviceUtils {
  val osVersion: String
    get() = android.os.Build.VERSION.SDK_INT.toString()

  val deviceModel: String
    get() = String.format(
      "Android: %s,%s,%s", Build.MANUFACTURER, Build.MODEL,
      Build.VERSION.RELEASE
    )

  fun getDeviceUid(@NonNull context: Context): String = Settings.Secure.getString(
    context.applicationContext.contentResolver,
    Settings.Secure.ANDROID_ID
  )
}
