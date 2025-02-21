package com.hontail.data.model.request

data class CommentRequest(
    val commentCreatedAt: String,
    val commentId: Int,
    val content: String,
    val userEmail: String,
    val userId: Int,
    val userImageUrl: String,
    val userNickname: String
)