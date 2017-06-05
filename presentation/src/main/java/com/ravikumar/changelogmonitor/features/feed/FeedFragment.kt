package com.ravikumar.changelogmonitor.features.feed

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ravikumar.changelogmonitor.ChangelogApplication
import com.ravikumar.changelogmonitor.R
import com.ravikumar.changelogmonitor.features.changelogdetail.ChangelogDetailActivity
import com.ravikumar.changelogmonitor.framework.extensions.textResource
import com.ravikumar.changelogmonitor.helpers.events.NewWatchlistEvent
import com.ravikumar.changelogmonitor.helpers.events.UserEvent
import com.ravikumar.entities.Changelog
import com.ravikumar.entities.Repository
import dagger.android.support.DaggerFragment
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.empty.emptyTextView
import kotlinx.android.synthetic.main.error.errorTextView
import kotlinx.android.synthetic.main.fragment_feed.feedFlipper
import kotlinx.android.synthetic.main.fragment_feed.feedRecyclerview
import kotlinx.android.synthetic.main.fragment_feed.feedRefreshLayout
import kotlinx.android.synthetic.main.progress.progressContentText
import kotlinx.android.synthetic.main.toolbar.toolbar
import javax.inject.Inject

class FeedFragment : DaggerFragment(), FeedContract.View {
  // region DI
  @Inject lateinit var presenter: FeedPresenter<FeedContract.View>
  // endregion

  // region Variables
  private val disposables = CompositeDisposable()
  lateinit var adapter: FeedAdapter
  // endregion

  // region Lifecycle
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_feed, container, false)
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    toolbar.title = getString(R.string.title_feed)
    setupRecyclerView()
    setListeners()
    presenter.onAttach(this)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    presenter.onDetach()
    disposables.clear()
  }
  // endregion

  // region View
  override fun showLoading(textRes: Int) {
    feedFlipper.displayedChild = INDEX_PAGE_PROGRESS
    progressContentText.textResource = textRes
  }

  override fun hideLoading() {
  }

  override fun showEmptyState(
    drawableRes: Int,
    textRes: Int
  ) {
    feedFlipper.displayedChild = INDEX_EMPTY_STATE
    emptyTextView.textResource = textRes
    feedRefreshLayout.isRefreshing = false
  }

  override fun showErrorState(
    drawableRes: Int,
    textRes: Int
  ) {
    feedFlipper.displayedChild = INDEX_ERROR_STATE
    errorTextView.textResource = textRes
    feedRefreshLayout.isRefreshing = false
  }

  override fun showFeed(
    changelogs: List<Changelog>,
    repositories: List<Repository>
  ) {
    feedFlipper.displayedChild = INDEX_DATA
    adapter.addItems(changelogs, repositories)
    feedRefreshLayout.isRefreshing = false
  }
  // endregion

  // region Private
  private fun setupRecyclerView() {
    adapter = FeedAdapter().apply {
      setHasStableIds(true)
    }

    val linearLayoutManager = LinearLayoutManager(activity)
    val dividerItemDecoration = DividerItemDecoration(context, linearLayoutManager.orientation)
    val drawable = ContextCompat.getDrawable(activity!!, R.drawable.divider)
    drawable?.let {
      dividerItemDecoration.setDrawable(it)
    }

    with(feedRecyclerview) {
      layoutManager = linearLayoutManager
      setHasFixedSize(false)
      adapter = this@FeedFragment.adapter
      addItemDecoration(dividerItemDecoration)
    }

    adapter.viewDetailClickListener = { changelog, repository ->
      activity?.let {
        startActivity(
          ChangelogDetailActivity.start(
            it, changelog.repoId, "${repository.owner}/${repository.name}",
            repository.url
          )
        )
      }
    }
  }

  private fun setListeners() {
    subscribeToEvents()
    setSwipeRefreshListener()
  }

  private fun subscribeToEvents() {
    val disposable = ChangelogApplication
      .rxBus.asFlowable()
      .subscribe {
        when (it) {
          it as? NewWatchlistEvent,
          it as? UserEvent -> presenter.fetchFeed()
        }
      }
    disposables.add(disposable)
  }

  private fun setSwipeRefreshListener() {
    feedRefreshLayout.setOnRefreshListener {
      presenter.fetchFeed()
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
    // endregion

    // region Instance
    fun newInstance(): FeedFragment {
      return FeedFragment()
    }
    // endregion
  }
  // endregion
}
