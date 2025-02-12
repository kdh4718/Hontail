package com.hontail.data.local

import androidx.room.Database
import com.hontail.data.local.dao.RecentCocktailDao
import com.hontail.data.model.dto.RecentCocktailIdTable

@Database(entities = [RecentCocktailIdTable::class], version = 1)
abstract class RecentCocktailIdDatabase {
    abstract fun recentCocktailIdDao(): RecentCocktailDao
}