package com.hontail.data.remote

import com.hontail.data.model.response.CocktailListResponse
import com.hontail.data.model.response.CocktailListResponseX
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PictureService {
    // 이미지 분석 칵테일 조회
    @GET("/api/vision/ingredient-analyze")
    suspend fun ingredientAnalyze(
        @Query("userId") userId: Int,
        @Query("ingredientNames") analyzeTextList: List<String>
    ): Response<List<CocktailListResponseX>>
}