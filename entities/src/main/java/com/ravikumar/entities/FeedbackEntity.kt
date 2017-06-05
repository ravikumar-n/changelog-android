package com.ravikumar.entities

import java.util.UUID

data class FeedbackRequest(
  val feedback: Feedback
)

data class Feedback(
  val id: UUID? = null,
  val userId: UUID,
  val deviceId: UUID,
  val feedback: String
)
