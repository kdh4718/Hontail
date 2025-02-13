package com.hontail.data.remote

import com.hontail.data.model.response.Comment
import com.hontail.data.model.response.CommentUpdateResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface CommentService {

    // 칵테일 댓글 수정
    @PUT("api/cocktail/detail/{cocktailId}/comments/{commentId}")
    suspend fun updateComment(
        @Path("cocktailId") cocktailId: Int,
        @Path("commentId") commentId: Int,
        @Body content: String
    ): Response<Void>

    // 칵테일 댓글 삭제
    @DELETE("api/cocktail/detail/{cocktailId}/comments/{commentId}")
    suspend fun deleteComment(
        @Path("cocktailId") cocktailId: Int,
        @Path("commentId") commentId: Int,
    ): Response<Void>

    // 칵테일 댓글 작성
    @POST("api/cocktail/detail/{cocktailId}/comment")
    suspend fun insertComment(
        @Path("cocktailId") cocktailId: Int,
        @Body content: String
    ): Response<Comment>

    // 칵테일 댓글 조회
    @GET("api/cocktail/detail/{cocktailId}/comments")
    suspend fun getComments(
        @Path("cocktailId") cocktailId: Int
    ): Response<List<Comment>>

}