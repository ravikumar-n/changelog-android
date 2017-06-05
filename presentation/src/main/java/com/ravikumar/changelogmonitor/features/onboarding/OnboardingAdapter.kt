package com.ravikumar.changelogmonitor.features.onboarding

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.ravikumar.changelogmonitor.R

class OnboardingAdapter() : PagerAdapter() {
  // region Variables
  private lateinit var context: Context
  private lateinit var layoutInflater: LayoutInflater
  // endregion

  // region Constructor
  constructor(context: Context) : this() {
    this.context = context
    layoutInflater = LayoutInflater.from(context)
  }
  // endregion

  // region Adapter
  override fun instantiateItem(
    collection: ViewGroup,
    position: Int
  ): Any {
    val layout = getPage(position, collection)
    collection.addView(layout)
    return layout
  }

  override fun destroyItem(
    collection: ViewGroup,
    position: Int,
    view: Any
  ) {
    collection.removeView(view as View)
  }

  override fun getCount(): Int {
    return IMAGE_COUNT
  }

  override fun isViewFromObject(
    view: View,
    any: Any
  ): Boolean {
    return view === any
  }
  // endregion

  // region Private
  private fun getPage(
    position: Int,
    parent: ViewGroup
  ): View {
    val onboardingLayout = layoutInflater
      .inflate(R.layout.onboarding, parent, false)
    val headerImage: ImageView = onboardingLayout.findViewById(R.id.headerImage)
    val titleText: TextView = onboardingLayout.findViewById(R.id.titleText)
    val contentText: TextView = onboardingLayout.findViewById(R.id.contentText)
    when (position) {
      0 -> {
        headerImage.setImageResource(R.drawable.onboarding_step_one)
        titleText.text = context.getString(R.string.step_one_subscribe)
        contentText.text = context.getText(R.string.step_one_subscribe_description)
      }

      1 -> {
        headerImage.setImageResource(R.drawable.onboarding_step_two)
        titleText.text = context.getString(R.string.step_two_notification)
        contentText.text = context.getText(R.string.step_two_notification_description)
      }
    }
    return onboardingLayout
  }
  // endregion

  // region Static
  companion object {
    // region Constants
    private const val IMAGE_COUNT = 2
    // endregion
  }
  // endregion
}
