package com.hontail.ui.picture.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hontail.data.model.response.Cocktail
import com.hontail.data.model.response.CocktailListResponse
import com.hontail.data.remote.RetrofitUtil
import kotlinx.coroutines.launch

private const val TAG = "CocktailPictureResultFr_SSAFY"

class CocktailPictureResultFragmentViewModel(private val handle: SavedStateHandle): ViewModel() {
    // Vision API로 분석한 텍스트 저장
    var ingredientList = handle.get<List<String>>("ingredientList") ?: emptyList()
        set(value){
            handle.set("ingredientList", value)
            field = value
        }

    // Vision API로 분석한 결과 칵테일 리스트
    private val _ingredientAnalyzeCoctailList = MutableLiveData<List<CocktailListResponse>>()
    val ingredientAnalyzeCoctailList: LiveData<List<CocktailListResponse>>
        get() = _ingredientAnalyzeCoctailList

    fun getIngredientAnalyze(){
        getIngredientAnalyze(ingredientList)
    }

    fun getIngredientAnalyze(analyzeList: List<String>){
        Log.d(TAG, "getIngredientAnalyze: ${analyzeList}")
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.pictureService.ingredientAnalyze(analyzeList)
            }.onSuccess {
                _ingredientAnalyzeCoctailList.value = it.body()
            }.onFailure {
                _ingredientAnalyzeCoctailList.value = emptyList()
            }
        }
    }
}