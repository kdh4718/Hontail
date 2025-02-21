package com.hontail.data.remote

import com.hontail.data.model.response.CocktailRecommendResponse
import com.hontail.data.model.response.CocktailResponse
import com.hontail.data.model.response.CocktailTopLikedResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CocktailService {

    // 좋아요 상위 10개 칵테일 조회
    @GET("api/cocktail/top-liked")
    suspend fun getCocktailTop10(): CocktailTopLikedResponse

    // 칵테일 필터링 조회
    @GET("api/cocktail/filtering")
    suspend fun getCocktailFiltering(
        @Query("orderBy") orderBy: String,
        @Query("direction") direction: String,
        @Query("baseSpirit") baseSpirit: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("isCustom") isCustom: Boolean
    ): CocktailResponse

    // 추천 칵테일 조회
    @GET("/recommend/{user_id}")
    suspend fun getRecommendedCocktail(
        @Path("user_id") userId: Int,
        @Query("top_n") top_n: Int
    ): CocktailRecommendResponse

    // 칵테일 이름으로 조회
    @GET("api/cocktail/search")
    suspend fun getCocktailByName(
        @Query("keyword") name: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): CocktailResponse

    @GET("api/mypage/me/likes/{user_id}")
    suspend fun getUserCocktailLikesCnt(
        @Path("user_id") userId: Int
    ): Int
}