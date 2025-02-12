package com.hontail.data.model.request

import com.google.gson.annotations.SerializedName

data class CustomCocktailRecipeRequest(
    @SerializedName("alcoholContent") val alcoholContent: Int,
    @SerializedName("baseSpirit") val baseSpirit: String,
    @SerializedName("cocktailName") val cocktailName: String,
    @SerializedName("description") val description: String,
    @SerializedName("imageUrl") val imageUrl: String,
    @SerializedName("ingredients") val ingredients: List<Ingredient>,
    @SerializedName("isCustom") val isCustom: Int,
    @SerializedName("makerNickname") val makerNickname: String,
    @SerializedName("recipes") val recipes: List<Recipe>
)