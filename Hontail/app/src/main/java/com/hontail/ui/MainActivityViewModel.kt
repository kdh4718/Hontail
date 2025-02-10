package com.hontail.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel : ViewModel() {
    // 기존 코드
    private val _cocktailId = MutableLiveData<Int>()
    val cocktailId: LiveData<Int>
        get() = _cocktailId

    fun setCocktailId(cocktailId: Int){
        _cocktailId.postValue(cocktailId)
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