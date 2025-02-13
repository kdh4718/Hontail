package com.hontail.ui.zzim.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hontail.data.model.request.LikeRequest
import com.hontail.data.model.response.CocktailListResponse
import com.hontail.data.remote.RetrofitUtil
import kotlinx.coroutines.launch

private const val TAG = "ZzimViewModel"
class ZzimViewModel: ViewModel() {

    private val likeService = RetrofitUtil.likeService

    // 찜한 칵테일 리스트
    private val _likedList = MutableLiveData<List<CocktailListResponse>>()
    val likedList: LiveData<List<CocktailListResponse>> get() = _likedList

    // 최근 본 칵테일 리스트
    private val _recentViewedList = MutableLiveData<List<CocktailListResponse>>()
    val recentViewedList: LiveData<List<CocktailListResponse>> get() = _recentViewedList

    init {
        getLikedRecentViewed()
    }


    // 좋아요(찜) 한 칵테일 리스트와 최근 본 칵테일 가져오기.
    fun getLikedRecentViewed() {

        viewModelScope.launch {
            try {

                val likeRequest = LikeRequest(
                    listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                )

                val response = likeService.getLikedRecentViewed(likeRequest)

                if(response.isSuccessful) {
                    response.body()?.let {
                        _likedList.value = it.likedCocktails
                        _recentViewedList.value = it.recentViewedCocktails
                    }
                }
            }
            catch(e: Exception) {
                android.util.Log.d(TAG, "getLikedRecentViewed: ${e.message}")
            }
        }
    }
}