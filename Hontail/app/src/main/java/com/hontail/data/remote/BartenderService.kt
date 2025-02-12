package com.hontail.data.remote

import com.hontail.data.model.request.BartenderRequest
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
        @Body bartenderRequest: BartenderRequest
    ): Response<BartenderResponse>

    // 바텐더로부터 초기 인삿말 받기.
    @GET("api/bartender/greeting")
    suspend fun receiveFromBartender(): Response<BartenderResponse>
}