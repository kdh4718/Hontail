package com.hontail.data.remote

import com.hontail.data.model.request.MyPageNicknameRequest
import com.hontail.data.model.response.MyPageCocktailResponse
import com.hontail.data.model.response.MyPageInformationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Query

interface MyPageService {

    // 닉네임 수정
    @PUT("api/mypage/me/nickname")
    suspend fun modifyUserNickname(
        @Body myPageNicknameRequest: MyPageNicknameRequest
    ): Response<MyPageInformationResponse>

    // 현재 사용자 정보 가져오기
    @GET("api/mypage/me")
    suspend fun getUserInformaion(): Response<MyPageInformationResponse>

    // 내가 만든 칵테일 조회
    @GET("api/mypage/cocktail")
    suspend fun getUserCustomCocktails(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<MyPageCocktailResponse>
}