package com.hontail.data.model.response

import java.sql.Timestamp

data class CocktailListResponse(
    val id: Int,
    val cocktailName: String,
    val imageUrl: String,
    val likes: Int,
    val alcoholContent: Int,
    val baseSpirit: String,
    val createdAt: String,
    val ingredientCount: Int,
    val isLiked: Boolean
)
