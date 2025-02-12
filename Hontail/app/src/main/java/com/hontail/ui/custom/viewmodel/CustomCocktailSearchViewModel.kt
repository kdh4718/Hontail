package com.hontail.ui.custom.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.hontail.data.local.IngredientRepository
import com.hontail.data.model.dto.IngredientsTable

private const val TAG = "CustomCocktailViewModel"
class CustomCocktailSearchViewModel: ViewModel() {

    private val ingredientRepository = IngredientRepository.getInstance()

    // 검색어를 관리하는 LiveData
    private val _searchQuery = MutableLiveData<String>()
    val searchQuery: LiveData<String> get() = _searchQuery

    // 검색어가 변경될 때마다 결과 가져오기.
    val searchResults: LiveData<List<IngredientsTable>> = _searchQuery.switchMap { query ->

        if(query.isBlank()) {
            MutableLiveData(emptyList())
        }
        else {
            ingredientRepository.getIngredientsByNameKor(query)
        }
    }

    // 외부에서 검색어 업데이트
    fun setSearchQuery(query: String) {
        Log.d(TAG, "setSearchQuery: $query")
        _searchQuery.value = query
    }

}