package com.hontail.data.model.response

data class CocktailDetailResponse(
    val cocktailDescription: Any,
    val cocktailId: Int,
    val cocktailName: String,
    val comments: List<Comment>,
    val imageUrl: String,
    val ingredients: List<Ingredient>,
    val likes: List<Like>,
    val recipes: List<Recipe>
)