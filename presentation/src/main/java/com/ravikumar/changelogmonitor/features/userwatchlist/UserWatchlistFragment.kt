package com.ravikumar.changelogmonitor.features.userwatchlist

import android.app.Activity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ravikumar.changelogmonitor.ChangelogApplication
import com.ravikumar.changelogmonitor.R
import com.ravikumar.changelogmonitor.features.watchlist.WatchlistActivity
import com.ravikumar.changelogmonitor.framework.customtabs.CustomTabActivityHelper
import com.ravikumar.changelogmonitor.framework.extensions.openInCustomTab
import com.ravikumar.changelogmonitor.framework.extensions.textResource
import com.ravikumar.changelogmonitor.helpers.events.NewWatchlistEvent
import com.ravikumar.changelogmonitor.helpers.events.UserEvent
import com.ravikumar.entities.Repository
import dagger.android.support.DaggerFragment
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.empty.emptyTextView
import kotlinx.android.synthetic.main.error.errorTextView
import kotlinx.android.synthetic.main.fragment_user_watchlist.fab
import kotlinx.android.synthetic.main.fragment_user_watchlist.userWatchlistRefreshLayout
import kotlinx.android.synthetic.main.fragment_user_watchlist.watchFlipper
import kotlinx.android.synthetic.main.fragment_user_watchlist.watchlistRecyclerView
import kotlinx.android.synthetic.main.progress.progressContentText
import kotlinx.android.synthetic.main.toolbar.toolbar
import javax.inject.Inject

class UserWatchlistFragment : DaggerFragment(), UserWatchlistContract.View {

  // region DI
  @Inject lateinit var presenter: UserWatchlistPresenter<UserWatchlistContract.View>
  // endregion

  // region Variables
  private val disposables = CompositeDisposable()
  lateinit var adapter: UserWatchlistAdapter
  private val customTab = CustomTabActivityHelper()
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
    return inflater.inflate(R.layout.fragment_user_watchlist, container, false)
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    toolbar.title = getString(R.string.your_watchlist)
    setListeners()
    setupRecyclerView()
    presenter.onAttach(this)
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
    presenter.onDetach()
    disposables.clear()
    super.onDestroyView()
  }
  // endregion

  // region View
  override fun showLoading(textRes: Int) {
    watchFlipper.displayedChild = INDEX_PAGE_PROGRESS
    progressContentText.textResource = textRes
  }

  override fun hideLoading() {
  }

  override fun showEmptyState(
    drawableRes: Int,
    textRes: Int
  ) {
    watchFlipper.displayedChild = INDEX_EMPTY_STATE
    emptyTextView.textResource = textRes
    hideSwipeRefreshProgress()
  }

  override fun showErrorState(
    drawableRes: Int,
    textRes: Int
  ) {
    watchFlipper.displayedChild = INDEX_ERROR_STATE
    errorTextView.textResource = textRes
    hideSwipeRefreshProgress()
  }

  override fun showWatchlist(items: List<Repository>) {
    if (watchFlipper.displayedChild != INDEX_DATA) {
      watchFlipper.displayedChild = INDEX_DATA
      hideSwipeRefreshProgress()
    }
    adapter.addItems(items)
  }
  // endregion

  // region Private
  private fun setListeners() {
    setFabListener()
    subscribeToEvents()
    setSwipeRefreshListener()
  }

  private fun setFabListener() {
    fab.setOnClickListener {
      activity?.let { context ->
        context.startActivityFromFragment(
          this,
          WatchlistActivity.newIntent(context, presenter.watchlist),
          REQUEST_SYNC_WATCHLIST
        )
      }
    }
  }

  private fun subscribeToEvents() {
    val disposable = ChangelogApplication
      .rxBus.asFlowable()
      .subscribe {
        when (it) {
          it as? NewWatchlistEvent,
          it as? UserEvent -> presenter.fetchWatchlist()
        }
      }
    disposables.add(disposable)
  }

  private fun setSwipeRefreshListener() {
    userWatchlistRefreshLayout.setOnRefreshListener {
      presenter.fetchWatchlist()
    }
  }

  private fun setupRecyclerView() {
    adapter = UserWatchlistAdapter()
    adapter.setHasStableIds(true)
    adapter.onItemClickListener = {
      @Suppress("CAST_NEVER_SUCCEEDS")
      it.url.openInCustomTab(activity as Activity)
    }

    val linearLayoutManager = LinearLayoutManager(activity)
    val dividerItemDecoration = DividerItemDecoration(context, linearLayoutManager.orientation)
    activity?.let {
      ContextCompat.getDrawable(it, R.drawable.divider)
        ?.let { dividerItemDecoration.setDrawable(it) }
    }

    with(watchlistRecyclerView) {
      layoutManager = linearLayoutManager
      addItemDecoration(DividerItemDecoration(activity, linearLayoutManager.orientation))
      setHasFixedSize(true)
      adapter = this@UserWatchlistFragment.adapter
      addItemDecoration(dividerItemDecoration)
    }
  }

  private fun hideSwipeRefreshProgress() {
    if (userWatchlistRefreshLayout.isRefreshing) {
      userWatchlistRefreshLayout.isRefreshing = false
    }
  }
  // endregion

  // region Static
  companion object {
    // region Constants
    private const val INDEX_DATA = 0
    private const val INDEX_PAGE_PROGRESS = 1
    private const val INDEX_EMPTY_STATE = 2
    private const val INDEX_ERROR_STATE = 3
    private const val REQUEST_SYNC_WATCHLIST = 101
    // endregion

    // region Instance
    fun newInstance(): UserWatchlistFragment {
      return UserWatchlistFragment()
    }
    // endregion
  }
  // endregion
}
