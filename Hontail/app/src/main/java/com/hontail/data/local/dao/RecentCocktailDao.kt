package com.hontail.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hontail.data.model.dto.RecentCocktailIdTable

@Dao
interface RecentCocktailDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCocktailId(cocktail: RecentCocktailIdTable)

    @Query("SELECT * FROM recent_cocktailId_table ORDER BY id DESC")
    suspend fun getAllCocktails(): List<RecentCocktailIdTable>

    @Query("DELETE FROM recent_cocktailId_table WHERE id = :id")
    suspend fun deleteCocktail(id: Int)

    @Query("DELETE FROM recent_cocktailId_table WHERE cocktailId NOT IN (SELECT cocktailId FROM recent_cocktailId_table ORDER BY id DESC LIMIT 10)")
    suspend fun deleteOldCocktails()

    @Query("SELECT * FROM recent_cocktailId_table WHERE cocktailId = :id LIMIT 1")
    suspend fun getCocktailById(id: Int): RecentCocktailIdTable?
}