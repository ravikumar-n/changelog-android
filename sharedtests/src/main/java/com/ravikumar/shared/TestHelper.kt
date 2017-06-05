package com.ravikumar.shared

import android.content.Context
import com.ravikumar.entities.AddWatchlist
import com.ravikumar.entities.ChangelogDetail
import com.ravikumar.entities.ChangelogResponse
import com.ravikumar.entities.DeviceInfo
import com.ravikumar.entities.Repositories
import com.ravikumar.entities.User
import com.ravikumar.entities.UserResponse
import com.ravikumar.entities.Watchlist
import java.util.UUID

class TestHelper @JvmOverloads constructor(context: Context? = null) {
  private val classLoader = if (context == null) {
    ClassLoader.getSystemClassLoader()
  } else {
    context.classLoader
  }

  fun getFakeUserResponse(): UserResponse {
    return UserResponse(getFakeUser())
  }

  fun getFakeUser() = User(
    id = UUID.fromString("f000aa01-0451-4000-b000-000000000000"),
    name = "Arya Stark"
  )

  fun getFakeProUser() = User(
    id = UUID.fromString("f000aa01-0451-4000-b000-000000000000"),
    name = "Arya Stark",
    subscriptionId = UUID.fromString("f000aa01-0451-4000-b000-000000000000")
  )

  /**
   * Returns fake repositories response which has 5 repositories for the
   * offsetIndex=5
   */
  fun getFakeRepositoriesResponse(): Repositories {
    val stream = classLoader.getResourceAsStream("repositories.json")
    return ChangelogJsonParser(stream).parseTo(Repositories::class.java)
  }

  fun getFakeWatchListIds(): AddWatchlist {
    val stream = classLoader.getResourceAsStream("watchlist_ids.json")
    return ChangelogJsonParser(stream).parseTo(AddWatchlist::class.java)
  }

  fun getFakeWatchList(): Watchlist {
    val stream = classLoader.getResourceAsStream("get_user_watchlist.json")
    return ChangelogJsonParser(stream).parseTo(Watchlist::class.java)
  }

  fun getFakeFeed(): ChangelogResponse {
    val stream = classLoader.getResourceAsStream("feed.json")
    return ChangelogJsonParser(stream).parseTo(ChangelogResponse::class.java)
  }

  fun getFakeChangelogs(): ChangelogResponse {
    val stream = classLoader.getResourceAsStream("changelogs.json")
    return ChangelogJsonParser(stream).parseTo(ChangelogResponse::class.java)
  }

  fun getFakeChangelogDetail(): ChangelogDetail {
    val stream = classLoader.getResourceAsStream("changelog_detail.json")
    return ChangelogJsonParser(stream).parseTo(ChangelogDetail::class.java)
  }

  fun getFakeDeviceInfo(): DeviceInfo {
    val stream = classLoader.getResourceAsStream("device.json")
    return ChangelogJsonParser(stream).parseTo(DeviceInfo::class.java)
  }
}
