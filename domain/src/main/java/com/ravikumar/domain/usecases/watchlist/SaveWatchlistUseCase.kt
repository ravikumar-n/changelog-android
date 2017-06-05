package com.ravikumar.domain.usecases.watchlist

import com.ravikumar.domain.datasource.ChangelogApi
import com.ravikumar.domain.executor.ExecutionScheduler
import com.ravikumar.domain.usecases.UseCase
import com.ravikumar.entities.Watchlist
import io.reactivex.Single
import java.util.UUID
import javax.inject.Inject

class SaveWatchlistUseCase @Inject constructor(
  private val api: ChangelogApi,
  private val scheduler: ExecutionScheduler
) : UseCase.RxSingle<Watchlist?, SaveWatchlistUseCase.Params>() {

  override fun build(params: Params?): Single<Watchlist?> {
    checkNotNull(params)
    return params!!.let {
      api
        .saveWatchlist(it.itemsToWatch)
        .map(::Watchlist)
        .compose(scheduler.highPrioritySingle())
    }
  }

  data class Params(val itemsToWatch: List<UUID>)
}
