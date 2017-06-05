package com.ravikumar.changelogmonitor.framework.utils

import android.content.Context
import android.support.annotation.NonNull
import android.view.View
import android.view.inputmethod.InputMethodManager

object ImeUtils {
  fun hideIme(@NonNull view: View) {
    val imm = view.context.getSystemService(
      Context.INPUT_METHOD_SERVICE
    ) as? InputMethodManager
    imm?.hideSoftInputFromWindow(view.windowToken, 0)
  }
}


