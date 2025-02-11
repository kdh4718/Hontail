package com.hontail.data.model.response

data class CocktailDetailResponse(
    val cocktailId: Int,
    val cocktailName: String,
    val cocktailDescription: String,
    val imageUrl: String,
    val makerNickname: String,
    val cocktailIngredients: List<Ingredient>,
    val recipes: List<Recipe>,
    val likes: Int,
    val comments: List<Comment>,
    val userId: Int,
    val isLiked: Boolean
)