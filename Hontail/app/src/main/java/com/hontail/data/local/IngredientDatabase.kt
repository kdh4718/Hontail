package com.hontail.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hontail.data.model.dto.IngredientsTable

@Database(entities = [IngredientsTable::class], version = 1)
abstract class IngredientDatabase: RoomDatabase() {

    abstract fun getIngredientDAO(): IngredientDAO
}