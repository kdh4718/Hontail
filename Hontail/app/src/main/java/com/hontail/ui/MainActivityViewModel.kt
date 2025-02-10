package com.hontail.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

private const val TAG = "MainActivityViewModel_SSAFY"

class MainActivityViewModel : ViewModel() {
    // 사용자 아이디
    private val _userId = MutableLiveData<Int>()
    val userId: LiveData<Int>
        get() = _userId

    fun setUserId(userI: Int){
        _userId.postValue(userI)
        Log.d(TAG, "setUserId: ${_userId.value} - ${userI}")
    }
    
    // 선택된 cocktailId
    private val _cocktailId = MutableLiveData<Int>()
    val cocktailId: LiveData<Int>
        get() = _cocktailId

    fun setCocktailId(cocktailId: Int){
        _cocktailId.postValue(cocktailId)
    }
}