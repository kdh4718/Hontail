package com.hontail.data.model.dto

import java.sql.Timestamp

data class CommentTable(
    val commentId: Int,
    val cocktailId: Int,
    val userId: Int,
    val content: String,
    val commentCreatedAt: Timestamp
)
