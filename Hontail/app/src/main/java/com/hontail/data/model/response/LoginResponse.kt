package com.hontail.data.model.response

data class LoginResponse(
    val refreshToken: String,
    val accessToken: String
)
