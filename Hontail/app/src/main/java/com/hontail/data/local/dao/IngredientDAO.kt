package com.hontail.data.local.dao

import androidx.lifecycle.LiveData
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

    @Query("SELECT * FROM ingredients WHERE ingredient_name_kor LIKE '%' || :query || '%'")
    fun getIngredientsByNameKor(query: String): LiveData<List<IngredientsTable>>

    @Query("SELECT * FROM ingredients WHERE ingredientId = :ingredientId")
    fun getIngredientsById(ingredientId: Int): LiveData<IngredientsTable>
}