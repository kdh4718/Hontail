package com.hontail.data.local

import android.content.Context
import com.hontail.data.local.dao.RecentCocktailDao
import com.hontail.data.model.dto.RecentCocktailIdTable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecentCocktailIdRepository private constructor(context: Context) {

    private val recentCocktailIdDao: RecentCocktailDao =
        RecentCocktailIdDatabase.getDatabase(context).recentCocktailIdDao()

    // 싱글톤 패턴을 사용하여 Repository 객체 관리
    companion object {
        @Volatile
        private var INSTANCE: RecentCocktailIdRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = RecentCocktailIdRepository(context)
                }
            }
        }

        fun getInstance(): RecentCocktailIdRepository {
            return INSTANCE ?: throw IllegalStateException("RecentCocktailIdRepository must be initialized")
        }
    }

    fun insertCocktail(cocktailId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            // 먼저 데이터베이스에서 이미 존재하는지 확인
            val existingCocktail = recentCocktailIdDao.getCocktailById(cocktailId)

            // 존재하지 않으면 새로운 cocktailId 추가
            if (existingCocktail == null) {
                val recentCocktailId = RecentCocktailIdTable(cocktailId = cocktailId)
                recentCocktailIdDao.insertCocktailId(recentCocktailId)
            } else {
                // 존재하면 기존 항목을 삭제하고 새로 삽입
                recentCocktailIdDao.deleteCocktail(cocktailId)
                val recentCocktailId = RecentCocktailIdTable(cocktailId = cocktailId)
                recentCocktailIdDao.insertCocktailId(recentCocktailId)
            }

            // 10개 이상일 경우 오래된 데이터 삭제
            deleteOldCocktails()
        }
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
}
