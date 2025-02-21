package com.hontail.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hontail.data.local.dao.IngredientDAO
import com.hontail.data.model.dto.IngredientsTable

@Database(entities = [IngredientsTable::class], version = 1)
abstract class IngredientDatabase: RoomDatabase() {

    abstract fun getIngredientDAO(): IngredientDAO
}