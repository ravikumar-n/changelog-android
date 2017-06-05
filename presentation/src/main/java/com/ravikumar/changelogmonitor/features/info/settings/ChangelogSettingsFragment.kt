package com.ravikumar.changelogmonitor.features.info.settings

import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.content.pm.PackageManager.NameNotFoundException
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.ShareCompat
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.ravikumar.changelogmonitor.APP_PLAYSTORE_URI
import com.ravikumar.changelogmonitor.BuildConfig
import com.ravikumar.changelogmonitor.ChangelogApplication
import com.ravikumar.changelogmonitor.R
import com.ravikumar.changelogmonitor.TWITTER_APP_URI
import com.ravikumar.changelogmonitor.TWITTER_BROWSER_URI
import com.ravikumar.changelogmonitor.framework.extensions.bindInt
import com.ravikumar.changelogmonitor.framework.utils.ImeUtils
import com.ravikumar.changelogmonitor.helpers.Analytics
import com.ravikumar.data.preferences.BooleanPreference
import com.ravikumar.data.preferences.IntPreference
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_settings.analyticsCheckBox
import kotlinx.android.synthetic.main.fragment_settings.analyticsLayout
import kotlinx.android.synthetic.main.fragment_settings.autoDayNightCheckBox
import kotlinx.android.synthetic.main.fragment_settings.autoDayNightLayout
import kotlinx.android.synthetic.main.fragment_settings.feedbackLayout
import kotlinx.android.synthetic.main.fragment_settings.nightLayout
import kotlinx.android.synthetic.main.fragment_settings.nightThemeCheckBox
import kotlinx.android.synthetic.main.fragment_settings.reviewLayout
import kotlinx.android.synthetic.main.fragment_settings.shareLayout
import kotlinx.android.synthetic.main.fragment_settings.twitterLayout
import kotlinx.android.synthetic.main.fragment_settings.versionSummaryTextView
import java.util.Locale
import javax.inject.Inject

class ChangelogSettingsFragment : DaggerFragment(),
  ChangelogSettingsContract.View, OnSharedPreferenceChangeListener {
  // region DI
  @Inject lateinit var presenter: ChangelogSettingsPresenter<ChangelogSettingsContract.View>
  @Inject lateinit var themePreference: IntPreference
  @Inject lateinit var analyticsPreference: BooleanPreference
  @Inject lateinit var preferences: SharedPreferences
  // endregion

  // region Variables
  private val lightTheme by bindInt(R.integer.theme_light)
  private val darkTheme by bindInt(R.integer.theme_dark)
  private val autoTheme by bindInt(R.integer.theme_daynight)
  // endregion

  // region Lifecycle
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    retainInstance = true
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_settings, container, false)
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    init()
    setListeners()
  }

  override fun onDestroyView() {
    super.onDestroyView()
    presenter.onDetach()
    preferences.unregisterOnSharedPreferenceChangeListener(this)
  }
  // endregion

  // region Overrides
  override fun onSharedPreferenceChanged(
    preference: SharedPreferences?,
    key: String?
  ) {
    if (key?.contentEquals(getString(R.string.preference_theme)) == true) {
      ChangelogApplication.analytics?.let {
        val theme = when (themePreference.get()) {
          autoTheme -> Analytics.THEME_AUTO
          darkTheme -> Analytics.THEME_DARK
          else -> Analytics.THEME_LIGHT
        }
        it.logSelectedTheme(theme)
      }
      restartApp()
    }
  }
  // endregion

  // region View
  override fun showLoading(textRes: Int) {
  }

  override fun hideLoading() {
  }

  override fun showEmptyState(
    drawableRes: Int,
    textRes: Int
  ) {
  }

  override fun showErrorState(
    drawableRes: Int,
    textRes: Int
  ) {
  }

  override fun onFeedbackSent() {
    Toast.makeText(activity, R.string.feedback_sent, Toast.LENGTH_SHORT)
      .show()
  }

  override fun onFailedToSendFeedback() {
    Toast.makeText(activity, R.string.error_failed_to_send_feedback, Toast.LENGTH_SHORT)
      .show()
  }
  // endregion

  // region Private methods
  private fun init() {
    presenter.onAttach(this)

    setupThemeSupport()
    setupAnalyticsSupport()
    setupTwitterSupport()
    setupShare()
    setupRateAndReview()
    setupVersion()
    setupFeedback()
  }

  private fun restartApp() {
    val intent = activity?.packageManager?.getLaunchIntentForPackage(activity?.packageName)
      ?.apply {
        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
    activity?.finish()
    startActivity(intent)
  }

  private fun setListeners() {
    preferences.registerOnSharedPreferenceChangeListener(this)
    autoDayNightLayout.setOnClickListener { onAutoThemeSelected() }
    nightLayout.setOnClickListener { onNightThemeSelected() }
  }

  private fun onAutoThemeSelected() {
    nightThemeCheckBox.isChecked = false
    if (themePreference.get() != autoTheme) {
      autoDayNightCheckBox.isChecked = true
      autoDayNightCheckBox.jumpDrawablesToCurrentState()

      disableNightLayout()
      autoTheme?.let { value -> themePreference.set(value) }
    } else {
      autoDayNightCheckBox.isChecked = false
      autoDayNightCheckBox.jumpDrawablesToCurrentState()

      enableNightLayout()
      lightTheme?.let { value -> themePreference.set(value) }
    }
  }

  private fun onNightThemeSelected() {
    autoDayNightCheckBox.isChecked = false
    if (themePreference.get() != darkTheme) {
      nightThemeCheckBox.isChecked = true

      darkTheme?.let { value -> themePreference.set(value) }
    } else {
      enableNightLayout()

      lightTheme?.let { value -> themePreference.set(value) }
    }
  }

  private fun setupThemeSupport() {
    when (themePreference.get()) {
      autoTheme -> {
        autoDayNightCheckBox.isChecked = true
        autoDayNightCheckBox.jumpDrawablesToCurrentState()
      }
      darkTheme -> {
        nightThemeCheckBox.isChecked = true
        nightThemeCheckBox.jumpDrawablesToCurrentState()
      }
    }
  }

  private fun disableNightLayout() {
    nightThemeCheckBox.isChecked = false
    nightThemeCheckBox.jumpDrawablesToCurrentState()
    nightLayout.isEnabled = false
    nightLayout.alpha = ALPHA_DISABLED
  }

  private fun enableNightLayout() {
    nightThemeCheckBox.isChecked = false
    nightThemeCheckBox.jumpDrawablesToCurrentState()
    nightLayout.isEnabled = true
    nightLayout.alpha = ALPHA_ENABLED
  }

  private fun setupAnalyticsSupport() {
    analyticsCheckBox.isChecked = analyticsPreference.get() ?: true
    analyticsCheckBox.jumpDrawablesToCurrentState()

    analyticsLayout.setOnClickListener {
      if (analyticsPreference.get() == true) {
        analyticsPreference.set(false)
      } else {
        analyticsPreference.set(true)
      }
      analyticsCheckBox.isChecked = analyticsPreference.get() ?: true
      analyticsCheckBox.jumpDrawablesToCurrentState()
    }
  }

  private fun setupTwitterSupport() {
    twitterLayout.setOnClickListener {
      var intent: Intent?
      try {
        context?.packageManager?.getPackageInfo("com.twitter.android", 0)
        intent = Intent(
          Intent.ACTION_VIEW,
          Uri.parse(TWITTER_APP_URI)
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      } catch (e: NameNotFoundException) {
        // No Twitter app, revert to browser
        intent = Intent(Intent.ACTION_VIEW, Uri.parse(TWITTER_BROWSER_URI))
      }
      startActivity(intent)
    }
  }

  private fun setupShare() {
    shareLayout.setOnClickListener {
      val shareIntent = ShareCompat.IntentBuilder.from(activity)
        .setText(getString(R.string.prompt_share_text))
        .setType("text/plain")
        .setSubject(getString(R.string.app_name))
        .intent

      startActivity(shareIntent)
      ChangelogApplication.analytics?.logShareEvent()
    }
  }

  private fun setupRateAndReview() {
    reviewLayout.setOnClickListener {
      val intent = Intent(Intent.ACTION_VIEW)
      intent.data = Uri.parse(APP_PLAYSTORE_URI)
      startActivity(intent)
    }
  }

  private fun setupVersion() {
    versionSummaryTextView.text = String.format(
      Locale.ENGLISH,
      "%s",
      BuildConfig.VERSION_NAME
    )
  }

  private fun setupFeedback() {
    feedbackLayout.setOnClickListener {
      val dialogView = View.inflate(activity, R.layout.dialog_feedback, null)
      val feedbackEditText = dialogView.findViewById<View>(R.id.feedback) as EditText

      val builder = context?.let {
        with(AlertDialog.Builder(it, R.style.ChangelogAlertDialog)) {
          setView(dialogView)
          setPositiveButton(R.string.dialog_send, null)
          setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
        }
      }

      val dialog = builder?.create()
        ?.apply {
          setTitle(R.string.feedback)
          setCancelable(true)
          show()
        }
      dialog?.getButton(AlertDialog.BUTTON_POSITIVE)
        ?.setOnClickListener { _ ->
          // To prevent dialog from dismissing on empty setupFeedback
          val feedback = feedbackEditText.text.toString()
          if (feedback.isEmpty()) {
            feedbackEditText.error = getString(R.string.error_empty_feedback)
          } else {
            ImeUtils.hideIme(dialogView)
            presenter.sendFeedback(feedback)
            dialog.dismiss()
          }
        }
    }
  }
  // endregion

  // region Static
  companion object {
    // region Constants
    private const val ALPHA_DISABLED = 0.6f
    private const val ALPHA_ENABLED = 1.0f
    // endregion

    // region Instance
    fun newInstance(): Fragment {
      return ChangelogSettingsFragment()
    }
    // endregion
  }
  // endregion
}
