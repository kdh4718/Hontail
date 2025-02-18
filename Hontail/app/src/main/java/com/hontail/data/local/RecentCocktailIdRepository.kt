package com.hontail.data.local

import android.content.Context
import androidx.room.Room
import com.hontail.data.local.dao.RecentCocktailDao
import com.hontail.data.model.dto.RecentCocktailIdTable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecentCocktailIdRepository private constructor(context: Context) {

    private val database = Room.databaseBuilder(
        context.applicationContext,
        RecentCocktailIdDatabase::class.java,
        "recent_cocktail_database.db"
    ).build()

    private val recentCocktailIdDao: RecentCocktailDao =
        database.getRecentCocktailIdDao()

    suspend fun insertCocktail(cocktailId: Int) {
        recentCocktailIdDao.deleteByCocktailId(cocktailId)
        recentCocktailIdDao.insertCocktailId(RecentCocktailIdTable(cocktailId = cocktailId))
        recentCocktailIdDao.deleteOldCocktails()
    }

    // 최근 칵테일 목록 가져오기 (List<Int>로 반환하도록 수정)
    suspend fun getAllCocktails(): List<Int> {
        val cocktailList = recentCocktailIdDao.getAllCocktails()
        return cocktailList.map { it.cocktailId }
    }

    // 리스트에서 10개 이상일 경우 오래된 데이터 삭제
    private suspend fun deleteOldCocktails() {
        recentCocktailIdDao.deleteOldCocktails()
    }

    // 싱글톤 패턴을 사용하여 Repository 객체 관리
    companion object {
        @Volatile
        private var INSTANCE: RecentCocktailIdRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = RecentCocktailIdRepository(context)
            }
        }

        fun getInstance(): RecentCocktailIdRepository {
            return INSTANCE
                ?: throw IllegalStateException("RecentCocktailIdRepository must be initialized")
        }
    }
}
