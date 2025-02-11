package com.hontail.data.model.response

data class Comment(
    val commentId: Int,
    val userId: Int,
    val userNickname: String,
    val userEmail: String,
    val userImageUrl: String,
    val content: String,
    val commentCreatedAt: String,
)