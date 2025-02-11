package com.hontail.data.remote

import com.hontail.data.model.response.CocktailResponse
import com.hontail.data.model.response.CocktailTopLikedResponse
import retrofit2.http.GET
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
}