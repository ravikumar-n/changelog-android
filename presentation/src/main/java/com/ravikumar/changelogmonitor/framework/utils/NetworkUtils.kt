package com.ravikumar.changelogmonitor.framework.utils

import android.content.Context
import android.net.ConnectivityManager
import android.support.annotation.NonNull

object NetworkUtils {
  fun isConnected(@NonNull context: Context): Boolean {
    val connectivityManager = context.applicationContext.getSystemService(
      Context.CONNECTIVITY_SERVICE
    ) as ConnectivityManager
    val activeNetworkInfo = connectivityManager.activeNetworkInfo
    return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting
  }
}
