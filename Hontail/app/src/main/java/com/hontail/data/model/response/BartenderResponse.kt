package com.hontail.data.model.response

data class BartenderResponse(
    val cocktail: Cocktail,
    val cocktailRecommendation: Boolean,
    val message: String
)