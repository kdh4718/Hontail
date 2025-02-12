package com.hontail.data.local

import com.hontail.data.local.dao.RecentCocktailDao
import com.hontail.data.model.dto.RecentCocktailIdTable

class RecentCocktailIdRepository(private val recentCocktailDao: RecentCocktailDao) {

    suspend fun insertCocktail(cocktailId: Int) {
        recentCocktailDao.insertCocktailId(cocktailId)
        recentCocktailDao.deleteOldCocktails()  // 10개 이상일 경우 오래된 데이터 삭제
    }

    suspend fun getCocktailIds(): List<Int> {
        return recentCocktailDao.getAllCocktails().map { it.cocktailId }
    }
}