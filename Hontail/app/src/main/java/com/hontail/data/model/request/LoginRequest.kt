package com.hontail.data.model.request

data class LoginRequest(
    val token: String,
    val provider: String
)
