package com.hontail.data.model.response

data class CocktailDetailResponse(
    val cocktailId: Int,
    val cocktailName: String,
    val cocktailDescription: String,
    val imageUrl: String,
    val userId: Int,
    val makerNickname: String,
    val alcoholContent: Int,
    val likeCnt: Int,
    val commentCnt: Int,
    val cocktailIngredients: List<CocktailIngredient>,
    val recipes: List<Recipe>,
    val isLiked: Boolean
)