package com.hontail.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hontail.data.local.dao.SearchHistoryDao

@Database(entities = [SearchHistoryDatabase::class], version = 1)
abstract class SearchHistoryDatabase: RoomDatabase() {
    abstract fun getSearchHistoryDao(): SearchHistoryDao
}