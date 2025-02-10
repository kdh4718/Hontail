package com.hontail.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel : ViewModel() {
    // 선택된 cocktailId
    private val _cocktailId = MutableLiveData<Int>()
    val cocktailId: LiveData<Int>
        get() = _cocktailId

    fun setCocktailId(cocktailId: Int){
        _cocktailId.postValue(cocktailId)
    }

    // 선택된 ingredientId
    private val _ingredientId = MutableLiveData<Int>()
    val ingredientId: LiveData<Int> get() = _ingredientId

    fun setIngredientId(ingredientId: Int) {
        _ingredientId.postValue(ingredientId)
    }
}