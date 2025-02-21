package com.hontail.data.model.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_history")
data class SearchHistoryTable(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @ColumnInfo(name = "searchHistory") val searchHistory: String
)