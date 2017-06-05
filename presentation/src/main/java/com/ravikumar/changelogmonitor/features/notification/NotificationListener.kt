package com.ravikumar.changelogmonitor.features.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.TaskStackBuilder
import android.support.v4.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ravikumar.changelogmonitor.R
import com.ravikumar.changelogmonitor.features.changelogdetail.ChangelogDetailActivity
import timber.log.Timber
import java.util.UUID

class NotificationListener : FirebaseMessagingService() {

  // region Lifecycle
  override fun onMessageReceived(remoteMessage: RemoteMessage?) {
    Timber.d("onMessageReceived : %s", remoteMessage?.data)

    remoteMessage?.data?.let {
      if (it[KEY_REPO_ID] == null) {
        Timber.wtf("onMessageReceived repo Id is null.")
        return
      }

      val changelogDetailIntent = ChangelogDetailActivity.start(
        this,
        UUID.fromString(it[KEY_REPO_ID]),
        it[KEY_NAME] ?: ""
      )
      changelogDetailIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

      val pendingIntent = TaskStackBuilder
        .create(this)
        .addNextIntentWithParentStack(changelogDetailIntent)
        .getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT)

      val notificationManager = getSystemService(
        Context.NOTIFICATION_SERVICE
      ) as NotificationManager

      val notification =
        NotificationCompat.Builder(
          applicationContext, getNotificationChannelId(notificationManager)
        )
          .setAutoCancel(true)
          .setSmallIcon(R.drawable.ic_notification)
          .setPriority(NotificationCompat.PRIORITY_HIGH)
          .setDefaults(NotificationCompat.DEFAULT_ALL)
          .setContentTitle(it[KEY_TITLE])
          .setContentText(it[KEY_BODY])
          .setContentIntent(pendingIntent)
          .setColor(ContextCompat.getColor(this, R.color.primary))
          .setCategory(NotificationCompat.CATEGORY_MESSAGE)
          .build()

      notificationManager.notify(it[KEY_REPO_ID]?.hashCode() ?: 1, notification)
    }
  }
  // endregion

  // region Private
  private fun getNotificationChannelId(notificationManager: NotificationManager): String =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val notificationChannel = notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID)
      if (notificationChannel == null) {
        val channel = NotificationChannel(
          NOTIFICATION_CHANNEL_ID,
          getString(R.string.notification_channel_name_changelog),
          NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)
      }
      NOTIFICATION_CHANNEL_ID
    } else NOTIFICATION_CHANNEL_ID
  // endregion

  // region Static
  companion object {
    // region Constants
    private const val KEY_TITLE = "title"
    private const val KEY_BODY = "body"
    private const val KEY_REPO_ID = "repo_id"
    private const val KEY_NAME = "name"
    private const val NOTIFICATION_CHANNEL_ID = "changelog_notification"
    // endregion
  }
  // endregion
}
