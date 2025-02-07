package com.hontail.data.model.dto

data class IngredientsTable(
    val ingredientId: Int,
    val ingredientName: String,
    val ingredientNameKor: String,
    val ingredientCategory: String,
    val ingredientCategoryKor: String,
    val ingredientType: String,
    val ingredientAlcoholContent: Int,
    val ingredientImage: String
)
