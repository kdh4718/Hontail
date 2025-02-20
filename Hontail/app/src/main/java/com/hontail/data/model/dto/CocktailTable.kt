package com.hontail.data.model.dto

import java.sql.Timestamp

data class CocktailTable(
    val cocktailId: Int,
    val userId: Int,
    val makerNickname: String,
    val cocktailName: String,
    val cocktailDescription: String,
    val alcoholContent: Int,
    val imageUrl: String,
    val isCustom: Int,
    val createdAt: Timestamp,
    val baseSpirit: String
)
