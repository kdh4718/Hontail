package com.hontail.data.model.response

data class Ingredient(
    val cocktailIngredientId: Int,
    val ingredientQuantity: String,
    val ingredient: IngredientX,
)