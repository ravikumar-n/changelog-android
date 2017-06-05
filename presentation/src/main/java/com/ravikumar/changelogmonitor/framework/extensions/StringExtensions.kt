package com.ravikumar.changelogmonitor.framework.extensions

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
