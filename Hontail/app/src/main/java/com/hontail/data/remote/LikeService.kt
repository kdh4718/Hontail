package com.hontail.data.remote

import com.hontail.data.model.response.CommentUpdateResponse
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface LikeService {

    // 칵테일에 좋아요 등록
    @POST("api/cocktail/detail/{cocktailId}/likes")
    suspend fun insertLike(
        @Path("cocktailId") cocktailId: Int,
        @Query("userId") userId: Int
    ): Response<CommentUpdateResponse>

    // 칵테일에 좋아요 해제
    @DELETE("api/cocktail/detail/{cocktailId}/likes")
    suspend fun deleteLike(
        @Path("cocktailId") cocktailId: Int,
        @Query("userId") userId: Int
    ): Response<Void>
}