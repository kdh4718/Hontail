package com.hontail.data.remote

import android.adservices.adid.AdId
import com.hontail.data.model.request.CustomCocktailRecipeRequest
import com.hontail.data.model.response.CommentUpdateResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface CustomCocktailService {

    // 커스텀 칵테일 등록
    @POST("api/custom-cocktail")
    suspend fun insertCustomCocktail(
        @Query("userId") userId: Int,
        @Body customCocktailRecipeRequest: CustomCocktailRecipeRequest
    ): Int

    // 커스텀 칵테일 삭제
    @DELETE("api/custom-cocktail/delete/{cocktailId}")
    suspend fun deleteCustomCocktail(
        @Path("cocktailId") cocktailId: Int
    ): Int

    // 커스텀 칵테일 수정
    @PUT("api/custom-cocktail/update/{cocktailId}")
    suspend fun updateCustomCocktail(
        @Path("cocktailId") cocktailId: Int,
        @Body customCocktailRecipeRequest: CustomCocktailRecipeRequest
    ): Int
}