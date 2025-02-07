package com.hontail.data.model.response

data class Comment(
    val commentCreatedAt: String,
    val commentId: Int,
    val content: String,
    val userEmail: String,
    val userId: Int,
    val userImageUrl: Any,
    val userNickname: String
)