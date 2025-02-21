package com.hontail.data.remote

import com.hontail.data.model.response.CocktailDetailResponse
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CocktailDetailService {

    // 칵테일 상세 정보 조회
    @GET("/api/cocktail/detail/{cocktailId}")
    suspend fun getCocktailDetail(
        @Path("cocktailId") cocktailId: Int,
        @Query("userId") userId: Int
    ): Response<CocktailDetailResponse>

    @POST("/api/cocktail/detail/{cocktailId}/likes")
    suspend fun addCocktailLikes(
        @Path("cocktailId") cocktailId: Int
    ): Response<Int>

    @DELETE("/api/cocktail/detail/{cocktailId}/likes")
    suspend fun deleteCocktailLikes(
        @Path("cocktailId") cocktailId: Int
    ): Response<Int>
}