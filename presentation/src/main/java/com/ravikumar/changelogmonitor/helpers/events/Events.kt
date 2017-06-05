package com.ravikumar.changelogmonitor.helpers.events

import com.ravikumar.entities.Repository

/**
 * Triggered when user watchlist is updated.
 */
data class NewWatchlistEvent(val watchlist: List<Repository>)

/**
 * Triggered when subscription is created or deleted.
 */
class SubscriptionChangeEvent

/**
 * Triggered when Guest user connects his/her account with
 * a valid Google Account.
 */
class UserEvent
