package com.hontail.data.remote

import com.hontail.data.model.response.BartenderResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface BartenderService {

    // 바텐더한테 메시지 보내기.
    @POST("api/bartender/chat")
    suspend fun sendToBartender(
        @Body userMessage: String,
    ): Response<BartenderResponse>

    // 바텐더로부터 초기 인삿말 받기.
    @GET("api/bartender/greeting")
    suspend fun receiveFromBartender(
        @Query("userId") userId: Int,
        @Query("nickname") nickname: String
    ): Response<BartenderResponse>
}