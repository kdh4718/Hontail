package com.hontail.ui.cocktail.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class CocktailDetailFragmentViewModel(private val handle: SavedStateHandle) : ViewModel() {
    var cocktailId = handle.get<Int>("cocktailId") ?: 0
        set(value) {
            handle.set("cocktailId", value)
            field = value
        }

    private val _userId = MutableLiveData<Int>()
    val userId: LiveData<Int>
        get() = _userId


    fun setUserId(userId: Int) {
        _userId.postValue(userId)
    }

    fun getCocktailDetailInfo(){
        getCocktailDetailInfo(cocktailId)
    }

    fun getCocktailDetailInfo(cocktailId: Int){
        viewModelScope.launch {
            runCatching {

            }.onSuccess {

            }.onFailure {

            }
        }
    }
}