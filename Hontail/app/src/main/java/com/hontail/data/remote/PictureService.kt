package com.hontail.data.remote

import com.hontail.data.model.response.Cocktail
import com.hontail.data.model.response.CocktailListResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface PictureService {
    // 이미지 분석 칵테일 조회
    @POST("/api/ingredient-analyze")
    suspend fun ingredientAnalyze(@Body analyzeTextList: List<String>): Response<List<CocktailListResponse>>
}