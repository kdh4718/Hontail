package com.hontail.data.model.response

data class LikeResponse(
    val likedCocktails: List<LikedCocktail>,
    val recentViewedCocktails: List<RecentViewedCocktail>
)