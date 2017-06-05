package com.ravikumar.changelogmonitor.features.userwatchlist

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.ravikumar.changelogmonitor.R
import com.ravikumar.changelogmonitor.framework.extensions.getHumanReadableTags
import com.ravikumar.changelogmonitor.framework.extensions.inflate
import com.ravikumar.entities.Repository
import kotlinx.android.synthetic.main.list_item_repository.view.descriptionTextView
import kotlinx.android.synthetic.main.list_item_repository.view.tagsTextView
import kotlinx.android.synthetic.main.list_item_repository.view.titleTextView

class UserWatchlistAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
  // region Variables
  private var onItemClickListener: ((Repository) -> Unit)? = null

  private val repositories: MutableList<Repository> = mutableListOf()
  // endregion

  // region Adapter
  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): RecyclerView.ViewHolder? {
    return when (viewType) {
      TYPE_REPOSITORY -> createRepositoryViewHolder(parent)
      else -> null
    }
  }

  override fun onBindViewHolder(
    viewHolder: RecyclerView.ViewHolder?,
    position: Int
  ) {
    when (getItemViewType(position)) {
      TYPE_REPOSITORY -> (viewHolder as? RepositoryViewHolder)?.bindRepository(
        repositories[position]
      )
    }
  }

  override fun getItemId(position: Int): Long {
    return repositories[position].id.leastSignificantBits
  }

  override fun getItemCount() = repositories.size

  override fun getItemViewType(position: Int): Int {
    return TYPE_REPOSITORY
  }
  // endregion

  // region Public
  fun addItems(newRepositories: List<Repository>) {
    repositories.clear()
    repositories.addAll(newRepositories)
    notifyDataSetChanged()
  }
  // endregion

  // region Private
  private fun createRepositoryViewHolder(parent: ViewGroup): RepositoryViewHolder {
    val repoHolder = RepositoryViewHolder(view = parent.inflate(R.layout.list_item_repository))
    repoHolder.itemView.setOnClickListener({
      if (repoHolder.adapterPosition != RecyclerView.NO_POSITION) {
        onItemClickListener?.invoke(repositories[repoHolder.adapterPosition])
      }
    })
    return repoHolder
  }
  // endregion

  // region  ViewHolders
  private class RepositoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    @SuppressLint("SetTextI18n")
    fun bindRepository(repository: Repository) {
      with(repository) {
        with(itemView) {
          descriptionTextView.text = description
          titleTextView.text = repository.owner + "/" + repository.name
          tagsTextView.text = repository.tags.getHumanReadableTags()
        }
      }
    }
  }
  // endregion

  // region Static
  companion object {
    // region Constants
    private const val TYPE_REPOSITORY = 1
    // endregion
  }
  // endregion
}
