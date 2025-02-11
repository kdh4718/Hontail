package com.hontail.data.model.request

data class CustomCocktailRecipeRequest(
    val alcoholContent: Int,
    val baseSpirit: String,
    val cocktailName: String,
    val description: String,
    val imageUrl: String,
    val ingredients: List<Ingredient>,
    val isCustom: Int,
    val makerNickname: String,
    val recipes: List<Recipe>
)