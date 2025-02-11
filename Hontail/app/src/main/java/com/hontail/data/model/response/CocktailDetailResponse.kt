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
){
    constructor() : this(
        cocktailId = 0,
        cocktailName = "",
        cocktailDescription = "",
        imageUrl = "",
        userId = 0,
        makerNickname = "",
        alcoholContent = 0,
        likeCnt = 0,
        commentCnt = 0,
        cocktailIngredients = emptyList(),
        recipes = emptyList(),
        isLiked = false
    )
}