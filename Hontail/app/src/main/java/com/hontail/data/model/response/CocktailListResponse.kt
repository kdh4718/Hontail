package com.hontail.data.model.response

data class CocktailListResponse(
    val id: Int,
    val cocktailName: String,
    val imageUrl: String,
    val likesCnt: Int,
    val alcoholContent: Int,
    val baseSpirit: String,
    val createdAt: String,
    val ingredientCount: Int,
    val isLiked: Boolean
)
