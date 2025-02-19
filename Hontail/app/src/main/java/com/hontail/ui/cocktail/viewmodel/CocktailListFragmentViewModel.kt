package com.hontail.ui.cocktail.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.hontail.data.model.response.CocktailListResponse
import com.hontail.data.remote.RetrofitUtil

private const val TAG = "CocktailListFragmentVie_SSAFY"

class CocktailListFragmentViewModel(private val handle: SavedStateHandle) : ViewModel() {
    private val _userId = MutableLiveData<Int>()
    val userId: LiveData<Int>
        get() = _userId

    var orderBy: String
        get() = handle.get<String>("orderBy") ?: "id"
        set(value) {
            handle.set("orderBy", value)
        }

    var direction: String
        get() = handle.get<String>("direction") ?: "ASC"
        set(value) {
            handle.set("direction", value)
        }

    var baseSpirit: String
        get() = handle.get<String>("baseSpirit") ?: ""
        set(value) {
            handle.set("baseSpirit", value)
        }

    var page: Int
        get() = handle.get<Int>("page") ?: 0
        set(value) {
            handle.set("page", value)
        }

    var totalPage: Int = 0

    var size: Int
        get() = handle.get<Int>("size") ?: 20
        set(value) {
            handle.set("size", value)
        }

    var isCustom: Boolean
        get() = handle.get<Boolean>("isCustom") ?: false
        set(value) {
            handle.set("isCustom", value)
        }

    private val _cocktailList = MutableLiveData<List<CocktailListResponse>>()
    val cocktailList: LiveData<List<CocktailListResponse>>
        get() = _cocktailList

    private val _cocktailLikesCnt = MutableLiveData<Int>()
    val cocktailLikesCnt: LiveData<Int>
        get() = _cocktailLikesCnt


    fun getUserCocktailLikesCnt(){
        getUserCocktailLikesCnt(userId.value ?: 0)
    }

    fun getUserCocktailLikesCnt(userId: Int){
        Log.d(TAG, "Likes fwefwefwefgetUserCocktailLikesCnt: ")

        viewModelScope.launch {
            runCatching {
                RetrofitUtil.cocktailService.getUserCocktailLikesCnt(userId)
            }.onSuccess {
                _cocktailLikesCnt.value = it
                Log.d(TAG, "Likes Success getUserCocktailLikesCnt: ${_cocktailLikesCnt.value}")
            }.onFailure {
                Log.d(TAG, "Likes Failure getUserCocktailLikesCnt: ${it.message}")
                _cocktailLikesCnt.value = 0
            }
        }
    }

    // ✅ MutableSharedFlow 사용하여 이벤트 트리거 가능하게 변경
    private val _filterRequestFlow = MutableSharedFlow<Unit>(replay = 0)

    init {
        viewModelScope.launch {
            _filterRequestFlow
                .debounce(100) // 0.1초 동안 추가 호출 없을 때 실행
                .collectLatest {
                    fetchCocktailFiltering()
                }
        }
    }

    fun setUserId(userId: Int) {
        _userId.value = userId
    }

    fun getCocktailFiltering() {
        viewModelScope.launch {
            _filterRequestFlow.emit(Unit) // ✅ SharedFlow의 emit()을 사용해 항상 이벤트 트리거
        }
    }

    private suspend fun fetchCocktailFiltering() {
        Log.d(TAG, "Filter fetchCocktailFiltering: orderBy - $orderBy, direction - $direction, baseSpirit - $baseSpirit, isCustom - $isCustom, page - $page")
        runCatching {
            RetrofitUtil.cocktailService.getCocktailFiltering(orderBy, direction, baseSpirit, page, size, isCustom)
        }.onSuccess {
            _cocktailList.postValue(it.content)
            totalPage = it.totalPages
            Log.d(TAG, "fetchCocktailFiltering: ${it.content}")
        }.onFailure {
            _cocktailList.postValue(emptyList())
            Log.d(TAG, "fetchCocktailFiltering: ${it.message}")
        }
    }
}
