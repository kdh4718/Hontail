package com.hontail.data.model.response

data class CocktailIngredient(
    val cocktailIngredientId: Int,
    val ingredientQuantity: String,
    val ingredient: IngredientX,
)