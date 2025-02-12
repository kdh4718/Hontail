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

    var userId = handle.get<Int>("userId") ?: 0
        set(value) {
            handle.set("userId", value)
            field = value
        }

    // Cocktail 정보도 LiveData로 관리
    private val _cocktailInfo = MutableLiveData<CocktailDetailResponse>()
    val cocktailInfo: LiveData<CocktailDetailResponse>
        get() = _cocktailInfo

    fun getCocktailDetailInfo(){
        getCocktailDetailInfo(cocktailId, userId)
    }

    fun getCocktailDetailInfo(cocktailId: Int, userId: Int){
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.cocktailDetailService.getCocktailDetail(cocktailId, userId)
            }.onSuccess {
                _cocktailInfo.value = it.body()
                Log.d(TAG, "getCocktailDetailInfo: ${_cocktailInfo.value}")
            }.onFailure {
                Log.d(TAG, "getCocktailDetailInfo: ${it.message}")
                _cocktailInfo.value = CocktailDetailResponse()
            }
        }
    }
}