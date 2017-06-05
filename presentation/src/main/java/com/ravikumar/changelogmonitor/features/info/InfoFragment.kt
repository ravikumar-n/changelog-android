package com.ravikumar.changelogmonitor.features.info

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar.OnMenuItemClickListener
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions
import com.ravikumar.changelogmonitor.ChangelogApplication
import com.ravikumar.changelogmonitor.R
import com.ravikumar.changelogmonitor.R.string
import com.ravikumar.changelogmonitor.SKU_HALF_YEARLY
import com.ravikumar.changelogmonitor.SKU_YEARLY
import com.ravikumar.changelogmonitor.features.info.settings.ChangelogSettingsFragment
import com.ravikumar.changelogmonitor.features.onboarding.OnboardingActivity
import com.ravikumar.changelogmonitor.features.subscriptions.SubscriptionActivity
import com.ravikumar.changelogmonitor.framework.extensions.bindInt
import com.ravikumar.changelogmonitor.helpers.events.SubscriptionChangeEvent
import com.ravikumar.changelogmonitor.helpers.events.UserEvent
import com.ravikumar.data.preferences.IntPreference
import com.ravikumar.entities.ChangelogSubscription
import com.ravikumar.entities.User
import dagger.android.support.DaggerFragment
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_info.settingsContainer
import kotlinx.android.synthetic.main.fragment_info.subscriptionPlanTextView
import kotlinx.android.synthetic.main.fragment_info.userAvatarImageView
import kotlinx.android.synthetic.main.fragment_info.userNameTextView
import kotlinx.android.synthetic.main.toolbar.toolbar
import javax.inject.Inject

class InfoFragment : DaggerFragment(), InfoContract.View, OnMenuItemClickListener {
  // region DI
  @Inject lateinit var presenter: InfoPresenter<InfoContract.View>
  @Inject lateinit var themePreference: dagger.Lazy<IntPreference>
  // endregion

  // region Variables
  private val disposables = CompositeDisposable()
  // endregion

  // region Lifecycle
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    retainInstance = true
    setHasOptionsMenu(true)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_info, container, false)
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    presenter.onAttach(this)
    setupToolbar()
    loadSettingsFragment()
    subscribeToEvents()
  }

  override fun onCreateOptionsMenu(
    menu: Menu,
    inflater: MenuInflater
  ) {
    toolbar.inflateMenu(R.menu.info)
    toolbar.setOnMenuItemClickListener(this)
    super.onCreateOptionsMenu(menu, inflater)
  }

  override fun onDestroy() {
    super.onDestroy()
    presenter.onDetach()
    disposables.clear()
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

  override fun showUserInfo(user: User) {
    userNameTextView.text = user.name
    loadUserAvatar(user.picture)
    subscriptionPlanTextView.text = getString(R.string.info_free_plan)
  }

  override fun showSubscription(subscription: ChangelogSubscription) {
    subscriptionPlanTextView.text = getString(
      when {
        subscription.productId == SKU_HALF_YEARLY -> R.string.info_half_yearly
        subscription.productId == SKU_YEARLY -> R.string.info_yearly
        else -> R.string.info_free_plan
      }
    )
  }

  override fun showFreePlan() {
    subscriptionPlanTextView.text = getString(R.string.info_free_plan)
  }

  override fun onUserSignedOut() {
    bindInt(R.integer.theme_light).value?.let {
      themePreference.get()
        .set(it)
    }

    activity?.let {
      startActivity(OnboardingActivity.start(it))
      it.finish()
    }
  }
  // endregion

  // region Overrides
  override fun onMenuItemClick(item: MenuItem?): Boolean {
    if (item?.itemId == R.id.upgrade) {
      activity?.let {
        startActivity(SubscriptionActivity.start(it))
      }
      return true
    } else if (item?.itemId == R.id.logout) {
      presenter.signout()
    }

    return false
  }
  // endregion

  // region Private
  private fun setupToolbar() {
    toolbar.title = getString(string.title_info)
    (activity as AppCompatActivity).setSupportActionBar(toolbar)
  }

  private fun loadSettingsFragment() {
    childFragmentManager.beginTransaction()
      ?.replace(settingsContainer.id, ChangelogSettingsFragment.newInstance())
      ?.commit()
  }

  private fun loadUserAvatar(url: String?) {
    val options = RequestOptions()
      .fitCenter()
      .placeholder(R.drawable.ic_account_circle_accent_24dp)
      .error(R.drawable.ic_account_circle_accent_24dp)
      .diskCacheStrategy(DiskCacheStrategy.ALL)
      .transforms(CircleCrop())
      .priority(Priority.LOW)

    Glide.with(this)
      .load(url)
      .apply(options)
      .transition(withCrossFade())
      .into(userAvatarImageView)
  }

  private fun subscribeToEvents() {
    val disposable = ChangelogApplication
      .rxBus.asFlowable()
      .subscribe {
        when (it) {
          it as? SubscriptionChangeEvent,
          it as? UserEvent -> presenter.fetchUserInfo()
        }
      }
    disposables.add(disposable)
  }
  // endregion

  // region Static
  companion object {
    // region Instance
    fun newInstance(): InfoFragment {
      return InfoFragment()
    }
    // endregion
  }
  // endregion
}
