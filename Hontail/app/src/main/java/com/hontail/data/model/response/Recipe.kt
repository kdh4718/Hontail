package com.hontail.data.model.response

data class Recipe(
    val recipeId: Int,
    val recipeGuide: String,
    val sequence: Int,
    val recipeAction: String
)