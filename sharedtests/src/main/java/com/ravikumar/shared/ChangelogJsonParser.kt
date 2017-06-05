package com.ravikumar.shared

import com.google.gson.FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES
import com.google.gson.GsonBuilder
import java.io.InputStream
import java.util.Scanner

class ChangelogJsonParser {
  private lateinit var stringToParse: String

  constructor(parseString: String) {
    stringToParse = parseString
  }

  constructor(stream: InputStream) {
    Scanner(stream).use {
      stringToParse = if (it.useDelimiter("\\A").hasNext()) it.next() else ""
    }
  }

  fun <T> parseTo(targetClass: Class<T>): T = targetClass.cast(
    gson.fromJson(stringToParse, targetClass)
  )

  companion object {
    private val gson = GsonBuilder().apply {
      setFieldNamingPolicy(LOWER_CASE_WITH_UNDERSCORES)
    }
      .create()
  }
}
