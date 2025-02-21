package com.hontail.data.remote

import com.hontail.data.model.request.LikeRequest
import com.hontail.data.model.response.CommentUpdateResponse
import com.hontail.data.model.response.LikeResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface LikeService {

    // 사용자가 좋아요 한 칵테일과 최근 본 칵테일 목록 조회
    @POST("api/cocktail/liked")
    suspend fun getLikedRecentViewed(
        @Body likeRequest: LikeRequest
    ): Response<LikeResponse>

    // 칵테일에 좋아요 등록
    @POST("api/cocktail/detail/{cocktailId}/likes")
    suspend fun insertLike(
        @Path("cocktailId") cocktailId: Int
    ): Int

    // 칵테일에 좋아요 해제
    @DELETE("api/cocktail/detail/{cocktailId}/likes")
    suspend fun deleteLike(
        @Path("cocktailId") cocktailId: Int
    ): Int
}