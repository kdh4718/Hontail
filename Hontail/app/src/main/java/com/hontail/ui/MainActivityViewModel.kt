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

    fun setUserId(userI: Int) {
        _userId.postValue(userI)
        Log.d(TAG, "setUserId: ${_userId.value} - ${userI}")
    }

    // 선택된 cocktailId
    private val _cocktailId = MutableLiveData<Int>()
    val cocktailId: LiveData<Int>
        get() = _cocktailId

    fun setCocktailId(cocktailId: Int) {
        _cocktailId.postValue(cocktailId)
    }

    // 선택된 ingredientId
    private val _ingredientId = MutableLiveData<Int>()
    val ingredientId: LiveData<Int> get() = _ingredientId

    fun setIngredientId(ingredientId: Int) {
        _ingredientId.postValue(ingredientId)
    }

    // 필터 관련 코드 추가
    private val _selectedZzimFilter = MutableLiveData<Int?>()
    val selectedZzimFilter: LiveData<Int?> = _selectedZzimFilter

    private val _selectedTimeFilter = MutableLiveData<Int?>()
    val selectedTimeFilter: LiveData<Int?> = _selectedTimeFilter

    private val _selectedAlcoholFilter = MutableLiveData<Int?>()
    val selectedAlcoholFilter: LiveData<Int?> = _selectedAlcoholFilter

    private val _selectedBaseFilter = MutableLiveData<Int?>()
    val selectedBaseFilter: LiveData<Int?> = _selectedBaseFilter

    private val _zzimButtonSelected = MutableLiveData<Boolean>()
    val zzimButtonSelected: LiveData<Boolean> = _zzimButtonSelected

    private val _timeButtonSelected = MutableLiveData<Boolean>()
    val timeButtonSelected: LiveData<Boolean> = _timeButtonSelected

    private val _alcoholButtonSelected = MutableLiveData<Boolean>()
    val alcoholButtonSelected: LiveData<Boolean> = _alcoholButtonSelected

    private val _baseButtonSelected = MutableLiveData<Boolean>()
    val baseButtonSelected: LiveData<Boolean> = _baseButtonSelected

    fun setZzimFilter(radioButtonId: Int) {
        _selectedZzimFilter.value = radioButtonId
    }

    fun setTimeFilter(radioButtonId: Int) {
        _selectedTimeFilter.value = radioButtonId
    }

    fun setAlcoholFilter(radioButtonId: Int) {
        _selectedAlcoholFilter.value = radioButtonId
    }

    fun setBaseFilter(radioButtonId: Int) {
        _selectedBaseFilter.value = radioButtonId
    }

    fun updateZzimButtonState(selected: Boolean) {
        _zzimButtonSelected.value = selected
    }

    fun updateTimeButtonState(selected: Boolean) {
        _timeButtonSelected.value = selected
    }

    fun updateAlcoholButtonState(selected: Boolean) {
        _alcoholButtonSelected.value = selected
    }

    fun updateBaseButtonState(selected: Boolean) {
        _baseButtonSelected.value = selected
    }
}