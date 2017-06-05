package com.ravikumar.changelogmonitor.features.feed

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.ravikumar.changelogmonitor.R
import com.ravikumar.changelogmonitor.framework.extensions.inflate
import com.ravikumar.entities.Changelog
import com.ravikumar.entities.Repository
import kotlinx.android.synthetic.main.list_item_changelog_feed.view.feedDescriptionTextView
import kotlinx.android.synthetic.main.list_item_changelog_feed.view.feedRepoTextView
import kotlinx.android.synthetic.main.list_item_changelog_feed.view.feedVersionTextView
import kotlinx.android.synthetic.main.list_item_changelog_feed.view.viewDetailButton
import ru.noties.markwon.Markwon
import java.util.UUID

class FeedAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
  // region Variables
  var viewDetailClickListener: ((Changelog, Repository) -> Unit)? = null

  private val items: MutableList<Changelog> = mutableListOf()
  private val repoMap: MutableMap<UUID, Repository> = mutableMapOf()
  // endregion

  // region Adapter
  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): RecyclerView.ViewHolder? {
    return changelogFeedViewHolder(parent)
  }

  override fun onBindViewHolder(
    viewHolder: RecyclerView.ViewHolder?,
    position: Int
  ) {
    viewHolder?.let {
      if (getItemViewType(position) == TYPE_CHANGELOG_FEED) {
        val repository = repoMap[items[position].repoId]
        repository?.let {
          (viewHolder as? ChangelogFeedViewHolder)
            ?.bindFeed(items[position], it)
        }
      }
    }
  }

  override fun getItemId(position: Int): Long {
    return items[position].repoId.leastSignificantBits
  }

  override fun getItemCount() = items.size

  override fun getItemViewType(position: Int): Int {
    return TYPE_CHANGELOG_FEED
  }
  // endregion

  // region Public
  fun addItems(
    changelogs: List<Changelog>,
    repositories: List<Repository>
  ) {
    repoMap.clear()
    items.clear()

    repoMap.putAll(repositories.map { it.id to it }.toMap())
    items.addAll(changelogs)

    notifyDataSetChanged()
  }
  // endregion

  // region Private
  private fun changelogFeedViewHolder(parent: ViewGroup): ChangelogFeedViewHolder {
    val holder = ChangelogFeedViewHolder(view = parent.inflate(R.layout.list_item_changelog_feed))
    holder.itemView.viewDetailButton.setOnClickListener({
      if (holder.adapterPosition != RecyclerView.NO_POSITION) {
        val changelog = items[holder.adapterPosition]
        viewDetailClickListener?.invoke(changelog, repoMap[changelog.repoId]!!)
      }
    })
    return holder
  }
  // endregion

  // region  ViewHolders
  private class ChangelogFeedViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    @SuppressLint("SetTextI18n")
    fun bindFeed(
      changelog: Changelog,
      repository: Repository
    ) {
      with(itemView) {
        feedVersionTextView.text = changelog.latestVersion
        feedRepoTextView.text = repository.owner + "/" + repository.name
        Markwon.setMarkdown(feedDescriptionTextView, changelog.latestChangelog)
      }
    }
  }
  // endregion

  // region Static
  companion object {
    // region Constants
    const val TYPE_CHANGELOG_FEED = 1
    // endregion
  }
  // endregion
}
