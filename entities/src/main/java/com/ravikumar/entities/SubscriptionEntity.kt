package com.ravikumar.entities

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.UUID

data class SubscriptionRequest(
  val subscription: ChangelogSubscription
)

@Entity(tableName = "Subscription")
data class ChangelogSubscription(
  @PrimaryKey
  var id: UUID = UUID.randomUUID(),
  var orderId: String? = null,
  var packageName: String? = null,
  var productId: String? = null, // sku
  var purchaseTime: Long = -1L,
  var purchaseToken: String? = null,
  var autoRenewing: Boolean = false,
  var paymentState: Int = -1,
  var cancelReason: Int = -1,
  var purchaseTimeMillis: Long = -1L,
  var expiryTimeMillis: Long = -1L,
  var userCancellationTimeMillis: Long = -1L
)

data class SubscriptionResponse(
  val subscription: ChangelogSubscription,
  val user: User
)
