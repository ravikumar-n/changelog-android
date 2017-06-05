package com.ravikumar.changelogmonitor.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v7.app.AlertDialog
import android.support.v7.view.ContextThemeWrapper
import android.view.Gravity
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextSwitcher
import android.widget.TextView
import android.widget.ViewSwitcher
import com.ravikumar.changelogmonitor.R
import timber.log.Timber

class ChangelogProgressDialog : DialogFragment() {
  // region DialogFragment
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    retainInstance = true
    isCancelable = false
  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val message: String? = if (arguments?.containsKey(KEY_MESSAGE) == true) {
      arguments?.getString(KEY_MESSAGE)
    } else {
      activity?.getString(R.string.progress_loading)
    }

    val view: View? = activity?.layoutInflater?.inflate(R.layout.dialog_progress, null)
    setUpViewSwitcher(view, message)

    val dialog: AlertDialog? = activity?.let {
      AlertDialog.Builder(it)
        .setView(view)
        .create()
    }
    return dialog!!
  }

  override fun show(
    manager: FragmentManager,
    tag: String
  ) {
    try {
      val transaction = manager.beginTransaction()
      transaction.add(this, tag)
        .addToBackStack(null)
      transaction.commitAllowingStateLoss()
    } catch (e: IllegalStateException) {
      Timber.e(e, "IllegalStateException while showing dialog")
    }
  }

  override fun onDestroyView() {
    if (dialog != null && retainInstance) {
      dialog.setDismissMessage(null)
    }
    super.onDestroyView()
  }
  // endregion

  // region Private
  private fun setUpViewSwitcher(
    view: View?,
    message: String?
  ) {
    val switcher = view?.findViewById<TextSwitcher>(R.id.textSwitcher)
    switcher?.let {
      with(it) {
        setFactory(viewFactory)
        inAnimation = AnimationUtils.loadAnimation(activity, android.R.anim.fade_in)
        outAnimation = AnimationUtils.loadAnimation(activity, android.R.anim.fade_out)
        setCurrentText(message)
      }
    }
  }

  @SuppressLint("RestrictedApi")
  private val viewFactory = ViewSwitcher.ViewFactory {
    val wrappedContext = ContextThemeWrapper(activity, R.style.ProgressTextStyle)
    val textView = TextView(wrappedContext, null, 0)
    textView.gravity = Gravity.TOP or Gravity.LEFT
    textView
  }
  // endregion

  // region Static
  companion object {
    // region Constants
    private const val TAG_PROGRESS_DIALOG = "progress_dialog"
    private const val KEY_MESSAGE = "message"
    // endregion

    // region Instance
    private fun start(message: String): ChangelogProgressDialog {
      val bundle = Bundle()
      bundle.putString(KEY_MESSAGE, message)

      val progressDialog = ChangelogProgressDialog()
      progressDialog.arguments = bundle
      return progressDialog
    }
    // endregion

    // region Public
    fun showDialog(activity: FragmentActivity, @StringRes id: Int): ChangelogProgressDialog? {
      return showDialog(activity, activity.getString(id))
    }

    /**
     * Helper method to show a progress dialog with a message.
     */
    @JvmOverloads
    fun showDialog(
      activity: FragmentActivity,
      message: String,
      tag: String = TAG_PROGRESS_DIALOG
    ): ChangelogProgressDialog? {
      val progressDialog = ChangelogProgressDialog.start(message)
      progressDialog.show(activity.supportFragmentManager, tag)
      return progressDialog
    }

    /**
     * Helper method to hide a progress dialog.
     */
    @JvmOverloads
    fun hideDialog(
      activity: FragmentActivity,
      tag: String = TAG_PROGRESS_DIALOG
    ) {
      val dialogFragment = activity.supportFragmentManager.findFragmentByTag(tag) as? DialogFragment
      dialogFragment?.dismissAllowingStateLoss()
    }
    // endregion
  }
  // endregion
}
