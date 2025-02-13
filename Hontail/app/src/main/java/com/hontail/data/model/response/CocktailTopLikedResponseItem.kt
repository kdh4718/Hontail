package com.hontail.data.model.response

data class CocktailTopLikedResponseItem(
    val id: Int,
    val cocktailName: String,
    val imageUrl: String,
    val likeCnt: Int,
    val alcoholContent: Int,
    val baseSpirit: String,
    val createdAt: String,
    val ingredientCount: Int,
    val rank: Int
)