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

    @Query("DELETE FROM recent_cocktailId_table WHERE cocktailId = :cocktailId")
    suspend fun deleteByCocktailId(cocktailId: Int)

    @Query("""DELETE FROM recent_cocktailId_table 
        WHERE id IN(
        SELECT id FROM recent_cocktailId_table ORDER BY id ASC LIMIT 1)
        AND (SELECT COUNT(*) FROM recent_cocktailId_table) > 10""")
    suspend fun deleteOldCocktails()

    @Query("SELECT * FROM recent_cocktailId_table WHERE cocktailId = :id LIMIT 1")
    suspend fun getCocktailById(id: Int): RecentCocktailIdTable?
}