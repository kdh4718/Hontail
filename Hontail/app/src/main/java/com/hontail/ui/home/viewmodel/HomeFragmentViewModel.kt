package com.hontail.ui.home.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hontail.data.model.response.CocktailTopLikedResponseItem
import com.hontail.data.remote.RetrofitUtil
import kotlinx.coroutines.launch

private const val TAG = "HomeFragmentViewModel_SSAFY"

class HomeFragmentViewModel: ViewModel() {
    private val _userId = MutableLiveData<Int>()
    val userId: LiveData<Int>
        get() = _userId

    private val _topTenCocktailInfo = MutableLiveData<List<CocktailTopLikedResponseItem>>()
    val topTenCocktailInfo: LiveData<List<CocktailTopLikedResponseItem>>
        get() = _topTenCocktailInfo

    fun setUserId(userId: Int) {
        _userId.postValue(userId)
    }

    fun getTopTenCocktail(){
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.cocktailService.getCocktailTop10()
            }.onSuccess {
                Log.d(TAG, "getTopTenCocktail: ${it}")
                _topTenCocktailInfo.value = it
            }.onFailure {
                _topTenCocktailInfo.value = emptyList()
            }
        }
    }
}