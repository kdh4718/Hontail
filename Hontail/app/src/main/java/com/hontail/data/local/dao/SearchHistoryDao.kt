package com.hontail.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.hontail.data.model.dto.SearchHistoryTable

@Dao
interface SearchHistoryDao {
    // 중복된 검색어가 있다면 삭제 후 삽입
    @Query("DELETE FROM search_history WHERE searchHistory = :name")
    suspend fun deleteByHistory(name: String)

    @Insert
    suspend fun insertSearch(search: SearchHistoryTable)

    // ID로 삭제
    @Query("DELETE FROM search_history WHERE id = :id")
    suspend fun deleteById(id: Int)

    // 모든 검색어 조회 (최신순)
    @Query("SELECT * FROM search_history ORDER BY id DESC")
    suspend fun getAllSearches(): List<SearchHistoryTable>

    @Query("""
    DELETE FROM search_history 
    WHERE id IN (
        SELECT id FROM search_history ORDER BY id ASC LIMIT 1
    ) 
    AND (SELECT COUNT(*) FROM search_history) > 10
""")
    suspend fun deleteOldestIfNeeded()

}
