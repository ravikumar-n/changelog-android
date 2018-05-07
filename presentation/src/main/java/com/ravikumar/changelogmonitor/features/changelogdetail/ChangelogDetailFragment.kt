package com.ravikumar.changelogmonitor.features.changelogdetail

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar.OnMenuItemClickListener
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ravikumar.changelogmonitor.R
import com.ravikumar.changelogmonitor.framework.customtabs.CustomTabActivityHelper
import com.ravikumar.changelogmonitor.framework.extensions.openInCustomTab
import com.ravikumar.changelogmonitor.framework.extensions.statusBarHeight
import com.ravikumar.entities.Repository
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_changelog_detail.changelogToolbar
import kotlinx.android.synthetic.main.fragment_changelog_detail.detailTextView
import ru.noties.markwon.Markwon
import java.util.UUID
import javax.inject.Inject

class ChangelogDetailFragment : DaggerFragment(),
  ChangelogDetailContract.View,
  OnMenuItemClickListener {
  // region DI
  @Inject lateinit var presenter: ChangelogDetailPresenter<ChangelogDetailContract.View>
  // endregion

  // region Variables
  private var repoId: UUID? = null
  private var repoTitle: String? = null
  private var repoUrl: String? = null
  private val customTab = CustomTabActivityHelper()
  // endregion

  // region Lifecycle
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setHasOptionsMenu(true)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_changelog_detail, container, false)
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    parseDataFromBundle()
    setupToolbar()
    initialize()
    presenter.onAttach(this)
    fetchRepository()
    fetchChangelogDetail()
  }

  override fun onCreateOptionsMenu(
    menu: Menu,
    inflater: MenuInflater
  ) {
    changelogToolbar.inflateMenu(R.menu.changelog_detail)
    changelogToolbar.setOnMenuItemClickListener(this)
    super.onCreateOptionsMenu(menu, inflater)
  }

  override fun onStart() {
    super.onStart()
    customTab.bindCustomTabsService(activity as Activity)
  }

  override fun onStop() {
    customTab.unbindCustomTabsService(activity as Activity)
    super.onStop()
  }

  override fun onDestroyView() {
    customTab.setConnectionCallback(null)
    presenter.onDetach()
    super.onDestroyView()
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

  override fun showRepository(repository: Repository) {
    repoUrl = repository.url
    activity?.invalidateOptionsMenu()
  }

  override fun showChangelog(changelog: String) {
    Markwon.setMarkdown(detailTextView, changelog)
  }

  override fun showUserHint(@StringRes res: Int) {
    Toast.makeText(context, res, Toast.LENGTH_LONG)
      .show()
  }
  // endregion

  // region Overrides
  override fun onMenuItemClick(item: MenuItem?): Boolean {
    when (item?.itemId) {
      android.R.id.home -> {
        activity?.finish()
        return true
      }

      R.id.openBrowser -> {
        activity?.let {
          repoUrl?.openInCustomTab(activity as Activity)
        }
        return true
      }
    }
    return false
  }
  // endregion

  // region Private
  private fun parseDataFromBundle() {
    repoId = arguments?.getSerializable(PARAM_REPO_ID) as? UUID
    repoTitle = arguments?.getString(PARAM_NAME, getString(R.string.title_changelog_detail))
    repoUrl = arguments?.getString(PARAM_URL)
  }

  private fun setupToolbar() {
    with(activity as AppCompatActivity) {
      setSupportActionBar(changelogToolbar)
      supportActionBar?.setDisplayHomeAsUpEnabled(true)
      supportActionBar?.title = repoTitle ?: getString(R.string.title_changelog_detail)
      changelogToolbar?.setPadding(0, statusBarHeight(), 0, 0)
    }
  }

  private fun initialize() {
    customTab.setConnectionCallback(customTabConnect)
  }

  private fun fetchRepository() {
    if (TextUtils.isEmpty(repoUrl)) {
      // User may have started from notification
      repoId?.let { presenter.fetchRepository(it) }
    }
  }

  private fun fetchChangelogDetail() {
    repoId?.let { presenter.fetchChangelogDetail(it, repoTitle) }
  }
  // endregion

  // region Anonymous Inner classes
  private val customTabConnect = object : CustomTabActivityHelper.ConnectionCallback {
    override fun onCustomTabsConnected() {
      repoUrl?.let {
        customTab.mayLaunchUrl(Uri.parse(repoUrl), null, null)
      }
    }

    override fun onCustomTabsDisconnected() {}
  }
  // endregion

  // region Static
  companion object {
    // region Constants
    const val PARAM_REPO_ID = "repoId"
    const val PARAM_NAME = "name"
    const val PARAM_URL = "url"
    // endregion

    // region Instance
    fun newInstance(extras: Bundle): ChangelogDetailFragment {
      val fragment = ChangelogDetailFragment()
      fragment.arguments = extras
      return fragment
    }
    // endregion
  }
  // endregion
}
