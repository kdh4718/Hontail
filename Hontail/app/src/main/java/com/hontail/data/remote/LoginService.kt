package com.hontail.data.remote

import com.hontail.data.model.request.LoginRequest
import com.hontail.data.model.response.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {
    @POST("/api/login")
    suspend fun socialLogin(@Body request: LoginRequest): Response<LoginResponse>
}