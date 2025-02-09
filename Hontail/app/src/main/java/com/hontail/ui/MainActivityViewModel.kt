package com.hontail.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hontail.data.model.response.Cocktail
import com.hontail.data.remote.RetrofitUtil
import kotlinx.coroutines.launch

class MainActivityViewModel : ViewModel() {
    // 선택된 cocktailId
    private val _cocktailId = MutableLiveData<Int>()
    val cocktailId: LiveData<Int>
        get() = _cocktailId

    // Vision API로 분석한 텍스트 저장
    private val _ingredientList = MutableLiveData<List<String>>()
    val ingredientList: LiveData<List<String>>
        get() = _ingredientList

    fun setCocktailId(cocktailId: Int){
        _cocktailId.postValue(cocktailId)
    }

    fun setIngredientList(ingredientList: List<String>){
        _ingredientList.postValue(ingredientList)
    }
}