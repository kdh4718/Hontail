package com.hontail.data.remote

import com.hontail.data.model.dto.IngredientsTable
import retrofit2.http.GET

interface IngredientService {

    // 재료 모든 데이터 가져오기.
    @GET("api/ingredients")
    suspend fun getAllIngredients(): List<IngredientsTable>
}