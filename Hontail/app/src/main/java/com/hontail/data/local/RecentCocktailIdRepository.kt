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

    // 최근 칵테일 아이디 저장
    fun insertCocktail(cocktailId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val recentCocktailId = RecentCocktailIdTable(cocktailId = cocktailId)
            recentCocktailIdDao.insertCocktailId(recentCocktailId)
            deleteOldCocktails()
        }
    }

    // 최근 칵테일 목록 가져오기 (List<Int>로 반환하도록 수정)
    suspend fun getAllCocktails(): List<Int> {
        val cocktailList = recentCocktailIdDao.getAllCocktails()
        // RecentCocktailIdTable 객체에서 id만 추출하여 반환
        return cocktailList.map { it.id }  // id를 List<Int>로 반환
    }

    // 리스트에서 10개 이상일 경우 오래된 데이터 삭제
    private suspend fun deleteOldCocktails() {
        recentCocktailIdDao.deleteOldCocktails()
    }
}
