package com.ravikumar.changelogmonitor.features.watchlist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Toast
import com.ravikumar.changelogmonitor.ChangelogApplication
import com.ravikumar.changelogmonitor.R
import com.ravikumar.changelogmonitor.framework.extensions.createIntent
import com.ravikumar.changelogmonitor.framework.extensions.statusBarHeight
import com.ravikumar.changelogmonitor.framework.extensions.textResource
import com.ravikumar.changelogmonitor.framework.recyclerview.EndlessRecyclerViewScrollListener
import com.ravikumar.changelogmonitor.helpers.events.NewWatchlistEvent
import com.ravikumar.changelogmonitor.ui.ChangelogProgressDialog
import com.ravikumar.entities.Meta
import com.ravikumar.entities.Repository
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_watchlist.repoToolbar
import kotlinx.android.synthetic.main.activity_watchlist.repositoriesRecyclerView
import kotlinx.android.synthetic.main.activity_watchlist.syncWatchlistButton
import kotlinx.android.synthetic.main.activity_watchlist.viewFlipper
import kotlinx.android.synthetic.main.empty.emptyTextView
import kotlinx.android.synthetic.main.error.errorTextView
import kotlinx.android.synthetic.main.progress.progressContentText
import javax.inject.Inject

class WatchlistActivity : DaggerAppCompatActivity(), WatchlistContract.View {

  // region DI
  @Inject lateinit var presenter: WatchlistPresenter<WatchlistContract.View>
  @Inject lateinit var adapter: WatchlistAdapter
  // endregion

  // region Variables
  private var meta: Meta? = null
  // endregion

  // region Lifecycle
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_watchlist)
    parseDataFromBundle()
    setupToolbar()
    setupRecyclerView()
    setListeners()
    presenter.onAttach(this)
    presenter.fetchRepositories(offsetIndex = 0)
  }

  override fun onDestroy() {
    super.onDestroy()
    presenter.onDetach()
  }
  // endregion

  // region View
  override fun showLoading(textRes: Int) {
    viewFlipper.displayedChild = INDEX_PAGE_PROGRESS
    progressContentText.textResource = textRes
  }

  override fun hideLoading() {
  }

  override fun showEmptyState(
    drawableRes: Int,
    textRes: Int
  ) {
    viewFlipper.displayedChild = INDEX_EMPTY_STATE
    emptyTextView.textResource = textRes
  }

  override fun showErrorState(
    drawableRes: Int,
    textRes: Int
  ) {
    viewFlipper.displayedChild = INDEX_ERROR_STATE
    errorTextView.textResource = textRes
  }

  override fun showRepositories(
    repositories: List<Repository>,
    meta: Meta
  ) {
    if (viewFlipper.displayedChild != INDEX_DATA) {
      viewFlipper.displayedChild = INDEX_DATA
    }
    adapter.addItems(repositories)
    this.meta = meta
  }

  override fun showProgressDialog(textRes: Int) {
    ChangelogProgressDialog.showDialog(this, textRes)
  }

  override fun hideProgressDialog() {
    ChangelogProgressDialog.hideDialog(this)
  }

  override fun onWatchlistSavedSuccessfully(
    textRes: Int,
    watchlist: List<Repository>
  ) {
    Toast.makeText(this, textRes, Toast.LENGTH_SHORT)
      .show()
    ChangelogApplication.rxBus.send(NewWatchlistEvent(watchlist))
    finish()
  }

  override fun onFailedToSaveWatchlist(error: Pair<Int, String?>) {
    Toast.makeText(this, error.second ?: getString(error.first), Toast.LENGTH_LONG)
      .show()
  }
  // endregion

  // region Private
  private fun parseDataFromBundle() {
    val watchListIds: ArrayList<String> = intent.getStringArrayListExtra(ARG_WATCHLIST)
    adapter.setUserWatchlist(watchListIds)
  }

  private fun setupToolbar() {
    setSupportActionBar(repoToolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    supportActionBar?.title = getString(R.string.watchlist)
    repoToolbar?.setPadding(0, statusBarHeight(), 0, 0)
  }

  private fun setupRecyclerView() {
    adapter.setHasStableIds(true)

    val linearLayoutManager = LinearLayoutManager(this)
    val dividerItemDecoration = DividerItemDecoration(this, linearLayoutManager.orientation)
    ContextCompat.getDrawable(this, R.drawable.divider)
      ?.let { dividerItemDecoration.setDrawable(it) }

    with(repositoriesRecyclerView) {
      layoutManager = linearLayoutManager
      setHasFixedSize(true)
      adapter = this@WatchlistActivity.adapter
      addItemDecoration(dividerItemDecoration)
    }
    setRecyclerViewClickListener()
    setRecyclerViewScrollListener(linearLayoutManager)
  }

  private fun setRecyclerViewClickListener() {
    adapter.onItemClickListener = {
      // TODO Take them to detail?
    }
  }

  private fun setRecyclerViewScrollListener(linearLayoutManager: LinearLayoutManager) {
    repositoriesRecyclerView.addOnScrollListener(
      object : EndlessRecyclerViewScrollListener(linearLayoutManager) {
        override fun onLoadMore(
          page: Int,
          totalItemsCount: Int,
          view: RecyclerView
        ) {
          meta?.let { (totalPages, currentPage) ->
            if (!adapter.showLoadingMore && currentPage < totalPages) {
              presenter.fetchRepositories(adapter.itemCount)
              repositoriesRecyclerView.post { adapter.dataStartedLoading() }
            }
          }
        }
      })
  }

  private fun setListeners() {
    syncWatchlistButton.setOnClickListener {
      adapter.getSelectedRepositories()
        ?.let(presenter::saveWatchlist)
    }
  }
  //endregion

  // region Static
  companion object {
    // region Constants
    private const val INDEX_DATA = 0
    private const val INDEX_PAGE_PROGRESS = 1
    private const val INDEX_EMPTY_STATE = 2
    private const val INDEX_ERROR_STATE = 3
    private const val ARG_WATCHLIST = "watchlist"
    // endregion

    // region Intent
    fun newIntent(
      context: Context,
      repositories: List<Repository>
    ): Intent {
      val ids: List<String> = repositories.map { it -> it.id.toString() }
      return context.createIntent(
        WatchlistActivity::class.java,
        ARG_WATCHLIST to ids as ArrayList<String>
      )
    }
    // endregion
  }
  // endregion
}
