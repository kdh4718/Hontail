package com.hontail.ui.cocktail.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hontail.data.local.RecentCocktailIdDatabase
import com.hontail.data.local.RecentCocktailIdRepository
import com.hontail.data.model.response.CocktailDetailResponse
import com.hontail.data.remote.RetrofitUtil
import kotlinx.coroutines.launch

private const val TAG = "CocktailDetailFragmentV_SSAFY"

class CocktailDetailFragmentViewModel(private val handle: SavedStateHandle) : ViewModel() {

    private val customCocktailService = RetrofitUtil.customCocktailService

    var cocktailId = handle.get<Int>("cocktailId") ?: 0
        set(value) {
            handle.set("cocktailId", value)
            field = value
        }

    var userId = handle.get<Int>("userId") ?: 0
        set(value) {
            handle.set("userId", value)
            field = value
        }

    // Cocktail 정보도 LiveData로 관리
    private val _cocktailInfo = MutableLiveData<CocktailDetailResponse>()
    val cocktailInfo: LiveData<CocktailDetailResponse>
        get() = _cocktailInfo

    // Repository 객체를 ViewModel 내에서 직접 생성
    private val recentCocktailIdRepository = RecentCocktailIdRepository.getInstance()

    fun addLikes(cocktailId: Int){
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.cocktailDetailService.addCocktailLikes(cocktailId)
            }.onSuccess {
                _cocktailInfo.value!!.likeCnt = it.body() ?: _cocktailInfo.value!!.likeCnt
            }.onFailure {
                Log.d(TAG, "addLikes: ${it.message}")
            }
        }
    }

    fun deleteLikes(cocktailId: Int){
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.cocktailDetailService.deleteCocktailLikes(cocktailId)
            }.onSuccess {
                _cocktailInfo.value!!.likeCnt = it.body() ?: _cocktailInfo.value!!.likeCnt
            }.onFailure {
                Log.d(TAG, "deleteLikes: ${it.message}")
            }
        }
    }

    fun getCocktailDetailInfo() {
        Log.d(TAG, "getCocktailDetailInfo - cocktailId: ${cocktailId}")

        saveCocktailId(cocktailId)
        getCocktailDetailInfo(cocktailId, userId)
    }

    // RecentCocktailIdRepository에서 DB 작업을 runCatching으로 감싸서 예외를 처리
    private fun saveCocktailId(cocktailId: Int) {
        // Room에 cocktailId 저장
        viewModelScope.launch {
            runCatching {
                recentCocktailIdRepository.insertCocktail(cocktailId)
            }.onFailure {
                Log.e(TAG, "Error saving cocktailId to Room: ${it.message}")
            }
        }
        
        viewModelScope.launch { 
            runCatching {
                recentCocktailIdRepository.getAllCocktails()
            }.onSuccess {
                Log.d(TAG, "saveCocktailId: ${it}")
            }
        }
    }

    fun getCocktailDetailInfo(cocktailId: Int, userId: Int) {
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.cocktailDetailService.getCocktailDetail(cocktailId, userId)
            }.onSuccess {
                _cocktailInfo.value = it.body()
                Log.d(TAG, "getCocktailDetailInfo: ${_cocktailInfo.value}")
            }.onFailure {
                Log.d(TAG, "getCocktailDetailInfo: ${it.message}")
                _cocktailInfo.value = CocktailDetailResponse()
            }
        }
    }

    // 칵테일 삭제
    fun deleteCustomCocktail(cocktailId: Int) {
        viewModelScope.launch {
            runCatching {
                customCocktailService.deleteCustomCocktail(cocktailId)
            }.onSuccess {
                Log.d(TAG, "deleteCustomCocktail: 성공적으로 삭제")
            }.onFailure {
                Log.d(TAG, "deleteCustomCocktail: 실패 ${it.message}")
            }
        }
    }
}