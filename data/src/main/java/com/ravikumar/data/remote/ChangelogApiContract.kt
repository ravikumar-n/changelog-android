package com.ravikumar.data.remote

import com.ravikumar.entities.AddWatchlist
import com.ravikumar.entities.ChangelogDetail
import com.ravikumar.entities.ChangelogResponse
import com.ravikumar.entities.ChangelogSubscription
import com.ravikumar.entities.DeviceInfo
import com.ravikumar.entities.DeviceRequest
import com.ravikumar.entities.Feedback
import com.ravikumar.entities.FeedbackRequest
import com.ravikumar.entities.NewUser
import com.ravikumar.entities.Repositories
import com.ravikumar.entities.SubscriptionRequest
import com.ravikumar.entities.SubscriptionResponse
import com.ravikumar.entities.User
import com.ravikumar.entities.UserResponse
import com.ravikumar.entities.Watchlist
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.UUID

interface ChangelogApiContract {
  @GET("repositories")
  fun getRepositories(@Query("offsetIndex") index: Int): Observable<Repositories?>

  // region User
  @POST("user/guest")
  fun createGuestUser(@Body request: NewUser): Single<UserResponse?>

  @POST("user")
  fun createUser(@Body request: NewUser): Single<UserResponse?>

  @PUT("user")
  fun connectUser(@Body request: NewUser): Single<UserResponse?>
  // endregion

  // region Device
  @POST("device")
  fun registerDevice(@Body request: DeviceRequest): Single<DeviceInfo?>

  @PUT("{id}/device")
  fun updateDevice(
    @Path(
      "id"
    ) deviceId: UUID, @Body request: DeviceRequest
  ): Single<DeviceInfo?>
  // endregion

  // region Watchlist
  @GET("{id}/watchlist")
  fun getWatchlist(@Path("id") userId: UUID): Single<Watchlist?>

  @PUT("{id}/watchlist")
  fun saveWatchlist(@Path("id") userId: UUID, @Body items: AddWatchlist): Single<Watchlist?>
  // endregion

  // region Subscriptions
  @GET("{id}/subscriptions")
  fun getSubscription(@Path("id") userId: UUID): Single<ChangelogSubscription?>

  @POST("{id}/subscriptions")
  fun postSubscription(
    @Path(
      "id"
    ) userId: UUID, @Body request: SubscriptionRequest
  ): Single<SubscriptionResponse?>

  @DELETE("{id}/subscriptions")
  fun removeSubscription(@Path("id") userId: UUID): Single<User>
  // endregion

  // region Changelogs
  @GET("{id}/changelog")
  fun getChangelogFeed(@Path("id") userId: UUID): Single<ChangelogResponse?>

  @GET("changelog/{id}")
  fun getChangelogDetail(@Path("id") repoId: UUID): Single<ChangelogDetail>
  // endregion

  // region Feedback
  @POST("feedback")
  fun sendFeedback(@Body request: FeedbackRequest): Single<Feedback?>
  // endregion
}
