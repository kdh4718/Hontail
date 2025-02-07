package com.hontail.data.model.response

data class Ingredient(
    val cocktailIngredientId: Int,
    val ingredient: IngredientX,
    val ingredientQuantity: String
)