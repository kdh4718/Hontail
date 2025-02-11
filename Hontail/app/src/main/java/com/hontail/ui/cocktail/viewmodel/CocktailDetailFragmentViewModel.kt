package com.hontail.ui.cocktail.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hontail.data.model.response.CocktailDetailResponse
import com.hontail.data.remote.RetrofitUtil
import kotlinx.coroutines.launch

private const val TAG = "CocktailDetailFragmentV_SSAFY"

class CocktailDetailFragmentViewModel(private val handle: SavedStateHandle) : ViewModel() {
    var cocktailId = handle.get<Int>("cocktailId") ?: 0
        set(value) {
            handle.set("cocktailId", value)
            field = value
        }

    private val _userId = MutableLiveData<Int>()
    val userId: LiveData<Int>
        get() = _userId

    var cocktailInfo = handle.get<CocktailDetailResponse>("cocktailInfo")
        set(value) {
            handle.set("cocktailInfo", value)
            field = value
        }

    fun setUserId(userId: Int) {
        _userId.postValue(userId)
    }

    fun getCocktailDetailInfo(){
        getCocktailDetailInfo(cocktailId, _userId.value!!)
    }

    fun getCocktailDetailInfo(cocktailId: Int, userId: Int){
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.cocktailDetailService.getCocktailDetail(cocktailId, userId)
            }.onSuccess {
                cocktailInfo = it.body()
            }.onFailure {
                Log.d(TAG, "getCocktailDetailInfo: ${it.message}")
            }
        }
    }
}