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

    // 선택된 칵테일 베이스주 타입
    private val _baseSpirit = MutableLiveData<String>("")
    val baseSpirit: LiveData<String>
        get() = _baseSpirit

    fun setBaseSpirit(baseSpirit: String){
        _baseSpirit.postValue(baseSpirit)
    }
}