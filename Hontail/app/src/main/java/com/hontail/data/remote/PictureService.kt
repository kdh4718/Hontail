package com.hontail.data.remote

import com.hontail.data.model.response.Cocktail
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface PictureService {

    // 이미지 분석 칵테일 조회
    // Response 타입 이후 변경 필요
    @POST("/api/ingredient-analyze")
    suspend fun ingredientAnalyze(@Body analyzeTextList: List<String>): Response<List<Cocktail>>
}