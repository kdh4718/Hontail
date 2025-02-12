package com.hontail.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hontail.data.model.dto.RecentCocktailIdTable

@Dao
interface RecentCocktailDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCocktailId(cocktailId: Int)

    @Query("SELECT * FROM recent_cocktailId_table ORDER BY id DESC")
    suspend fun getAllCocktails(): List<RecentCocktailIdTable>

    @Query("DELETE FROM recent_cocktailId_table WHERE id = :id")
    suspend fun deleteCocktail(id: Int)

    @Query("DELETE FROM recent_cocktailId_table WHERE id NOT IN (SELECT id FROM recent_cocktailId_table ORDER BY id DESC LIMIT 10)")
    suspend fun deleteOldCocktails()
}