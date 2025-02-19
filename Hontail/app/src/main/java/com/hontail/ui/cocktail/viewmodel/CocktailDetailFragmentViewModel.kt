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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

private const val TAG = "CocktailDetailFragmentV_SSAFY"

class CocktailDetailFragmentViewModel(private val handle: SavedStateHandle) : ViewModel() {

    private val customCocktailService = RetrofitUtil.customCocktailService
    private val recentCocktailIdRepository = RecentCocktailIdRepository.getInstance()

    private val _cocktailIdFlow = MutableSharedFlow<Int>(replay = 1)  // 최신 값 유지
    val cocktailIdFlow = _cocktailIdFlow.asSharedFlow()

    var cocktailId = handle.get<Int>("cocktailId") ?: 0
        set(value) {
            handle.set("cocktailId", value)
            field = value
            viewModelScope.launch {
                _cocktailIdFlow.emit(value)  // 중복 방지된 값 emit
            }
        }

    init {
        observeCocktailIdChanges()
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

    private fun observeCocktailIdChanges() {
        viewModelScope.launch {
            cocktailIdFlow.filterNotNull().collectLatest { id ->
                saveCocktailId(id)
                getCocktailDetailInfo(id, userId)
            }
        }
    }

    fun addLikes(cocktailId: Int){
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.cocktailDetailService.addCocktailLikes(cocktailId)
            }.onSuccess {
                _cocktailInfo.value!!.likeCnt = it.body() ?: _cocktailInfo.value!!.likeCnt
                Log.d(TAG, "Detail addLikes: ${_cocktailInfo.value!!.likeCnt}")
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
                Log.d(TAG, "Detail deleteLikes: ${_cocktailInfo.value!!.likeCnt}")
            }.onFailure {
                Log.d(TAG, "deleteLikes: ${it.message}")
            }
        }
    }

    private suspend fun saveCocktailId(cocktailId: Int) {
        runCatching {
            recentCocktailIdRepository.insertCocktail(cocktailId)
        }.onFailure {
            Log.e(TAG, "Error saving cocktailId to Room: ${it.message}")
        }
        getRecentCoctailId()
    }

    fun getCocktailDetailInfo() {
        Log.d(TAG, "getCocktailDetailInfo - cocktailId: $cocktailId")
        viewModelScope.launch {
            _cocktailIdFlow.emit(cocktailId)  // 한 번만 emit
        }
    }

    private fun getRecentCoctailId(){
        viewModelScope.launch {
            runCatching {
                recentCocktailIdRepository.getAllCocktails()
            }.onSuccess {
                Log.d(TAG, "Recent saveCocktailId: ${it}")
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