package com.hontail.data.model.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ingredients")
data class IngredientsTable(
    @PrimaryKey(autoGenerate = true) val ingredientId: Int,
    @ColumnInfo(name = "ingredient_name") val ingredientName: String,
    @ColumnInfo(name = "ingredient_name_kor") val ingredientNameKor: String,
    @ColumnInfo(name = "ingredient_category") val ingredientCategory: String,
    @ColumnInfo(name = "ingredient_category_kor") val ingredientCategoryKor: String,
    @ColumnInfo(name = "ingredient_type") val ingredientType: String?,
    @ColumnInfo(name = "ingredient_alcohol_content") val ingredientAlcoholContent: Int,
    @ColumnInfo(name = "ingredient_image") val ingredientImage: String
)
