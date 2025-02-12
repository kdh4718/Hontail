package com.hontail.data.remote

import android.adservices.adid.AdId
import com.hontail.data.model.request.CustomCocktailRecipeRequest
import com.hontail.data.model.response.CommentUpdateResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface CustomCocktailService {

    @POST("api/custom-cocktail")
    suspend fun insertCustomCocktail(
        @Query("userId") userId: Int,
        @Body customCocktailRecipeRequest: CustomCocktailRecipeRequest
    ): Int
}