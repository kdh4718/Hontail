package com.hontail.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hontail.data.local.dao.RecentCocktailDao
import com.hontail.data.model.dto.RecentCocktailIdTable

@Database(entities = [RecentCocktailIdTable::class], version = 1)
abstract class RecentCocktailIdDatabase : RoomDatabase() {

    abstract fun recentCocktailIdDao(): RecentCocktailDao

    companion object {
        @Volatile
        private var INSTANCE: RecentCocktailIdDatabase? = null

        fun getDatabase(context: Context): RecentCocktailIdDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RecentCocktailIdDatabase::class.java,
                    "recent_cocktail_id_database.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
