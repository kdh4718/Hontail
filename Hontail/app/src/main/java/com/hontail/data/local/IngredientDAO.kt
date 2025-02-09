package com.hontail.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hontail.data.model.dto.IngredientsTable

@Dao
interface IngredientDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(ingredients: List<IngredientsTable>)

    @Query("SELECT * FROM ingredients")
    suspend fun getAllIngredients(): List<IngredientsTable>
}