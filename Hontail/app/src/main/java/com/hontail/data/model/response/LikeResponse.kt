package com.hontail.data.model.response

data class LikeResponse(
    val likedCocktails: List<CocktailListResponse>,
    val recentViewedCocktails: List<CocktailListResponse>
)