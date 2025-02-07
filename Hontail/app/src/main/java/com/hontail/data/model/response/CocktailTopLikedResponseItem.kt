package com.hontail.data.model.response

data class CocktailTopLikedResponseItem(
    val alcoholContent: Int,
    val baseSpirit: String,
    val cocktailName: String,
    val createdAt: String,
    val id: Int,
    val imageUrl: String,
    val ingredientCount: Int,
    val likes: Int
)