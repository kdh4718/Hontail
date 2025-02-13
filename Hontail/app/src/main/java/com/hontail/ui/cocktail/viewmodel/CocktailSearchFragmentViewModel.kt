package com.hontail.ui.cocktail.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hontail.data.model.response.CocktailListResponse
import com.hontail.data.remote.RetrofitUtil
import com.hontail.data.remote.RetrofitUtil.Companion.cocktailService
import kotlinx.coroutines.launch

private const val TAG = "CocktailSearchFragmentV_SSAFY"

class CocktailSearchFragmentViewModel(private val handle: SavedStateHandle): ViewModel() {
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

    private val _cocktailList = MutableLiveData<List<CocktailListResponse>>()
    val cocktailList: LiveData<List<CocktailListResponse>>
        get() = _cocktailList

    fun getCocktailByName(name: String){
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.cocktailService.getCocktailByName(name, page, size)
            }.onSuccess {
                _cocktailList.value = it.content
                Log.d(TAG, "getCocktailByName: ${_cocktailList.value}")
            }.onFailure {
                Log.d(TAG, "getCocktailByName: ${it.message}")
            }
        }

    }
}