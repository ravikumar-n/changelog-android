package com.ravikumar.changelogmonitor.features.onboarding

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.ravikumar.changelogmonitor.BuildConfig
import com.ravikumar.changelogmonitor.ChangelogApplication
import com.ravikumar.changelogmonitor.R
import com.ravikumar.changelogmonitor.features.dashboard.DashboardActivity
import com.ravikumar.changelogmonitor.framework.utils.DeviceUtils
import com.ravikumar.changelogmonitor.framework.utils.NetworkUtils
import com.ravikumar.changelogmonitor.helpers.Analytics
import com.ravikumar.changelogmonitor.ui.ChangelogProgressDialog
import com.ravikumar.data.preferences.FirebasePreferences
import com.ravikumar.entities.DeviceInfo
import com.ravikumar.entities.User
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_onboarding.googleSignIn
import kotlinx.android.synthetic.main.activity_onboarding.guestSignIn
import kotlinx.android.synthetic.main.activity_onboarding.pagerIndicator
import kotlinx.android.synthetic.main.activity_onboarding.viewPager
import javax.inject.Inject

class OnboardingActivity : DaggerAppCompatActivity(), OnboardingContract.View,
  OnSharedPreferenceChangeListener {

  // region DI
  @Inject lateinit var presenter: OnboardingPresenter<OnboardingContract.View>
  @Inject lateinit var firebasePreference: FirebasePreferences
  @Inject lateinit var preferences: SharedPreferences
  // endregion

  // region Variables
  private var signInClient: GoogleSignInClient? = null
  // endregion

  // region Lifecycle
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_onboarding)
    presenter.onAttach(this)
    setupOnboarding()
    setupGoogleSignInClient()
    setListeners()
  }

  override fun onActivityResult(
    requestCode: Int,
    resultCode: Int,
    data: Intent
  ) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == Companion.RC_SIGN_IN) { //
      val task = GoogleSignIn.getSignedInAccountFromIntent(data)
      if (task.isSuccessful) {
        task.result.let {
          presenter.createUser(it.idToken!!, it.displayName!!, it.email!!)
        }
      } else {
        Toast.makeText(this, R.string.error_google_sign_in_failed, Toast.LENGTH_LONG)
          .show()
      }
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    presenter.onDetach()
    preferences.unregisterOnSharedPreferenceChangeListener(this)
  }
  // endregion

  // region Overrides
  override fun onSharedPreferenceChanged(
    sharedPreferences: SharedPreferences?,
    key: String?
  ) {
    if (key == FirebasePreferences.PREFS_FIRE_BASE_TOKEN) {
      googleSignIn.isEnabled = true
      guestSignIn.isEnabled = true
    }
  }
  // endregion

  // region View
  override fun showLoading(textRes: Int) {
    ChangelogProgressDialog.showDialog(this, textRes)
  }

  override fun hideLoading() {
    ChangelogProgressDialog.hideDialog(this)
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

  override fun onGuestUserCreated(user: User) {
    presenter.registerDevice(getDeviceInfo(user))
  }

  override fun onGoogleSignIn(userName: String) {
    ChangelogProgressDialog.showDialog(this, getString(R.string.progress_s_create_user, userName))
  }

  override fun onUserCreated(user: User) {
    presenter.registerDevice(getDeviceInfo(user))
  }

  override fun onDeviceRegistered(deviceInfo: DeviceInfo) {
    startActivity(DashboardActivity.start(this))
    finish()
  }
  // endregion

  // region Private
  private fun setupOnboarding() {
    val adapter = OnboardingAdapter(this)
    viewPager.adapter = adapter
    viewPager.pageMargin = resources.getDimensionPixelSize(R.dimen.spacing_medium)
    pagerIndicator.setViewPager(viewPager)
    viewPager.setPageTransformer(true, OnboardingPageTransformer())
  }

  private fun setupGoogleSignInClient() {
    val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
      .requestIdToken(BuildConfig.SERVER_CLIENT_ID)
      .requestEmail()
      .build()

    signInClient = GoogleSignIn.getClient(this, options)
  }

  private fun setListeners() {
    if (firebasePreference.getFirebaseToken() == null) {
      googleSignIn.isEnabled = false
      guestSignIn.isEnabled = false

      if (!NetworkUtils.isConnected(this)) {
        Toast.makeText(this, R.string.alert_no_connection, Toast.LENGTH_LONG)
          .show()
      }
    }

    googleSignIn.setOnClickListener {
      signInClient?.signOut()
      val signInIntent = signInClient?.signInIntent
      startActivityForResult(signInIntent, RC_SIGN_IN)
      ChangelogApplication.analytics
        ?.logSignUp(Analytics.PARAM_SIGN_UP_METHOD_GOOGLE)
    }

    guestSignIn.setOnClickListener {
      presenter.createGuestUser()
      ChangelogApplication.analytics
        ?.logSignUp(Analytics.PARAM_SIGN_UP_METHOD_GUEST)
    }

    preferences.registerOnSharedPreferenceChangeListener(this)
  }

  private fun getDeviceInfo(user: User): DeviceInfo {
    return DeviceInfo(
      userId = user.id,
      uniqueId = DeviceUtils.getDeviceUid(this),
      modelInfo = DeviceUtils.deviceModel,
      platform = this.getString(R.string.platform),
      osVersion = DeviceUtils.osVersion,
      token = firebasePreference.getFirebaseToken()!!
    )
  }
  // endregion

  // region PageTransformer
  /**
   * See: https://android.jlelse.eu/creating-an-intro-screen-for-your-app-using-viewpager-pagetransformer-9950517ea04f
   */
  inner class OnboardingPageTransformer : ViewPager.PageTransformer {

    override fun transformPage(
      view: View,
      position: Float
    ) {

      // Get the page index from the tag. This makes
      // it possible to know which page index you're
      // currently transforming - and that can be used
      // to make some important performance improvements.
      //int resource = (int) view.getTag();

      val pageWidth = view.width
      val pageWidthTimesPosition = pageWidth * position
      val absPosition = Math.abs(position)

      // Now it's time for the effects
      if (position <= -1.0f || position >= 1.0f) {
        // The page is not visible. This is a good place to stop
        // any potential work / animations you may have running.
      } else if (position == 0.0f) {
        // The page is selected. This is a good time to reset Views
        // after animations as you can't always count on the PageTransformer
        // callbacks to match up perfectly.
      } else {
        // The page is currently being scrolled / swiped. This is
        // a good place to show animations that react to the user's
        // swiping as it provides a good user experience.

        // Let's start by animating the title.
        // We want it to fade as it scrolls out
        val titleText: View = view.findViewById(R.id.titleText)
        titleText.alpha = 1.0f - absPosition

        // Now the description. We also want this one to
        // fade, but the animation should also slowly move
        // down and out of the screen
        val descriptionText: View = view.findViewById(R.id.contentText)
        descriptionText.alpha = 1.0f - absPosition

        // Now, we want the image to move to the right,
        // i.e. in the opposite direction of the rest of the
        // content while fading out
        val headerImage: ImageView? = view.findViewById(R.id.headerImage)

        // We're attempting to create an effect for a View
        // specific to one of the pages in our ViewPager.
        // In other words, we need to check that we're on
        // the correct page and that the View in question
        // isn't null.
        headerImage?.alpha = 1.0f - absPosition
        headerImage?.translationX = -pageWidthTimesPosition * X_SCALING_FACTOR

        // Finally, it can be useful to know the direction
        // of the user's swipe - if we're entering or exiting.
        // This is quite simple:
        if (position < 0) {
          // Create your out animation here
          headerImage?.translationX = -position * (pageWidth / 2) //Half the normal speed
        } else {
          // Create your in animation here
          headerImage?.translationX = position * (pageWidth / 2) //Half the normal speed
        }
      }
    }
  }
  // endregion

  // region Static
  companion object {
    // region Intent
    fun start(context: Context): Intent {
      return Intent(context, OnboardingActivity::class.java)
    }
    // endregion

    // region Constants
    private const val RC_SIGN_IN = 9001
    private const val X_SCALING_FACTOR = 1.5f
    // endregion
  }
  // endregion
}
