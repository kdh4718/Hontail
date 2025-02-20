package com.hontail.data.model.dto

data class CocktailIngredientsTable(
    val cocktailIngredientId: Int,
    val cocktailId: Int,
    val ingredientId: Int,
    val ingredientQuantity: String
)
