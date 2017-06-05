package com.ravikumar.changelogmonitor.features.changelogdetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import com.ravikumar.changelogmonitor.R
import com.ravikumar.changelogmonitor.base.BaseActivity
import com.ravikumar.changelogmonitor.framework.extensions.createIntent
import java.util.UUID

class ChangelogDetailActivity : BaseActivity() {
  // region Lifecycle
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_layout)
  }
  // endregion

  // region Override
  override fun fragment(): Fragment {
    return ChangelogDetailFragment.newInstance(intent.extras)
  }
  // endregion

  // region Static
  companion object {
    fun start(
      context: Context,
      repoId: UUID,
      name: String,
      url: String = ""
    ): Intent {
      return context.createIntent(
        ChangelogDetailActivity::class.java,
        ChangelogDetailFragment.PARAM_REPO_ID to repoId,
        ChangelogDetailFragment.PARAM_NAME to name,
        ChangelogDetailFragment.PARAM_URL to url
      )
    }
  }
  // endregion
}
