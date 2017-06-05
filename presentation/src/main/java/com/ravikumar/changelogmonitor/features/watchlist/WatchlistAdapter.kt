package com.ravikumar.changelogmonitor.features.watchlist

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.ravikumar.changelogmonitor.R
import com.ravikumar.changelogmonitor.framework.extensions.getHumanReadableTags
import com.ravikumar.changelogmonitor.framework.extensions.inflate
import com.ravikumar.entities.Repository
import kotlinx.android.synthetic.main.infinite_loading.view.loading
import kotlinx.android.synthetic.main.list_item_repository.view.checkMarkImage
import kotlinx.android.synthetic.main.list_item_repository.view.descriptionTextView
import kotlinx.android.synthetic.main.list_item_repository.view.tagsTextView
import kotlinx.android.synthetic.main.list_item_repository.view.titleTextView
import java.util.UUID

class WatchlistAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
  // region Variables
  var showLoadingMore = false
  var onItemClickListener: ((Repository) -> Unit)? = null

  private val repositories: MutableList<Repository> = mutableListOf()
  private var watchlist: HashSet<UUID> = hashSetOf()
  // endregion

  // region Adapter
  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): RecyclerView.ViewHolder? {
    return when (viewType) {
      TYPE_REPOSITORY -> createRepositoryViewHolder(parent)
      TYPE_LOADING_MORE -> LoadingMoreHolder(view = parent.inflate(R.layout.infinite_loading))
      else -> null
    }
  }

  override fun onBindViewHolder(
    viewHolder: RecyclerView.ViewHolder?,
    position: Int
  ) {
    when (getItemViewType(position)) {
      TYPE_REPOSITORY -> {
        val repository = repositories[position]
        (viewHolder as? RepositoryViewHolder)?.bindRepository(
          repository, watchlist.contains(repository.id)
        )
      }
      TYPE_LOADING_MORE -> (viewHolder as? LoadingMoreHolder)?.bindLoading()
    }
  }

  override fun getItemId(position: Int): Long {
    return when (TYPE_LOADING_MORE) {
      getItemViewType(position) -> TYPE_LOADING_MORE.toLong()
      else -> repositories[position].id.leastSignificantBits
    }
  }

  override fun getItemCount() = repositories.size + if (showLoadingMore) 1 else 0

  override fun getItemViewType(position: Int): Int {
    return when {
      position < repositories.size && repositories.isNotEmpty() -> TYPE_REPOSITORY
      else -> TYPE_LOADING_MORE
    }
  }
  // endregion

  // region Public
  fun addItems(newRepositories: List<Repository>) {
    val oldValue: Int = repositories.size
    val insertIndex = if (oldValue > 0) repositories.size else oldValue
    dataFinishedLoading()
    repositories.addAll(newRepositories)
    notifyItemRangeInserted(insertIndex, newRepositories.size)
  }

  fun setUserWatchlist(watchlistIds: List<String>) {
    watchlist.clear()
    watchlistIds
      .map { it -> UUID.fromString(it) }
      .map(watchlist::add)
  }

  fun dataStartedLoading() {
    if (!showLoadingMore) {
      showLoadingMore = true
      notifyItemInserted(getLoadingMoreItemPosition())
    }
  }

  private fun dataFinishedLoading() {
    if (showLoadingMore) {
      notifyItemRemoved(getLoadingMoreItemPosition())
      showLoadingMore = false
    }
  }

  fun getSelectedRepositories(): List<UUID>? {
    return watchlist.toList()
  }
  // endregion

  // region Private
  private fun createRepositoryViewHolder(parent: ViewGroup): RepositoryViewHolder {
    val repoHolder = RepositoryViewHolder(view = parent.inflate(R.layout.list_item_repository))
    repoHolder.itemView.setOnClickListener({
      if (repoHolder.adapterPosition != RecyclerView.NO_POSITION) {
        val repository = repositories[repoHolder.adapterPosition]
        if (watchlist.contains(repository.id)) {
          watchlist.remove(repository.id)
          repoHolder.itemView.checkMarkImage.visibility = View.INVISIBLE
        } else {
          watchlist.add(repository.id)
          repoHolder.itemView.checkMarkImage.visibility = View.VISIBLE
        }
        onItemClickListener?.invoke(repositories[repoHolder.adapterPosition])
      }
    })
    return repoHolder
  }

  private fun getLoadingMoreItemPosition(): Int {
    return if (showLoadingMore) itemCount - 1 else RecyclerView.NO_POSITION
  }
  // endregion

  // region  ViewHolders
  private class RepositoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    @SuppressLint("SetTextI18n")
    fun bindRepository(
      repository: Repository,
      selected: Boolean
    ) {
      with(repository) {
        with(itemView) {
          descriptionTextView.text = description
          titleTextView.text = repository.owner + "/" + repository.name
          tagsTextView.text = repository.tags.getHumanReadableTags()
          checkMarkImage.visibility = if (selected) View.VISIBLE else View.INVISIBLE
        }
      }
    }
  }

  private class LoadingMoreHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun bindLoading() {
      with(itemView) {
        loading.visibility = View.VISIBLE
      }
    }
  }
  // endregion

  // region Static
  companion object {
    // region Constants
    private const val TYPE_LOADING_MORE = -1
    private const val TYPE_REPOSITORY = 1
    // endregion
  }
  // endregion
}
