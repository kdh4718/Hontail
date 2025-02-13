package com.hontail.data.model.response

import com.google.gson.annotations.SerializedName

data class LikedCocktail(
    @SerializedName("alcoholContent") val alcoholContent: Int,
    @SerializedName("baseSpirit") val baseSpirit: String,
    @SerializedName("cocktailName") val cocktailName: String,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("id") val id: Int,
    @SerializedName("imageUrl") val imageUrl: String,
    @SerializedName("ingredientCount") val ingredientCount: Int,
    @SerializedName("isLiked") val isLiked: Boolean,
    @SerializedName("likeCnt") val likeCnt: Int
)