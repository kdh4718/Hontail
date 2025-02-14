package com.hontail.ui.cocktail.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hontail.data.model.response.CocktailListResponse
import com.hontail.data.paging.CocktailPagingSource
import com.hontail.data.remote.RetrofitUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

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

    private val _filterSelectedList = MutableLiveData<List<Boolean>>(listOf(false, false, false, false))
    val filterSelectedList: LiveData<List<Boolean>> get() = _filterSelectedList

    // Paging 데이터를 담을 LiveData 추가
    val pagedCocktailList: Flow<PagingData<CocktailListResponse>> =
        Pager(PagingConfig(
            pageSize = 20,
            prefetchDistance = 5,
            maxSize = 100
        )) {
            CocktailPagingSource(orderBy, direction, baseSpirit, isCustom)
        }.flow.cachedIn(viewModelScope) // ✅ 반드시 `cachedIn(viewModelScope)` 사용

    private val _cocktailList = MutableLiveData<List<CocktailListResponse>>()
    val cocktailList: LiveData<List<CocktailListResponse>>
        get() = _cocktailList

    fun setUserId(userId: Int) {
        _userId.postValue(userId)
    }


    fun getCocktailFiltering(){
        Log.d(TAG, "Filter getCocktailFiltering: orderBy - ${orderBy}, direction - ${direction}, baseSpirit - ${baseSpirit}, isCustom - ${isCustom}, page - ${page}")
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.cocktailService.getCocktailFiltering(orderBy, direction, baseSpirit, page, size, isCustom)
            }.onSuccess {
                _cocktailList.value = it.content
                Log.d(TAG, "getCocktailFiltering: ${it.content}")
            }.onFailure {
                _cocktailList.value = emptyList()
                Log.d(TAG, "getCocktailFiltering: ${it.message}")
            }
        }
    }
}
