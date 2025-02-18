package com.hontail.ui.zzim.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hontail.data.local.RecentCocktailIdRepository
import com.hontail.data.model.request.LikeRequest
import com.hontail.data.model.response.CocktailListResponse
import com.hontail.data.remote.RetrofitUtil
import kotlinx.coroutines.launch

private const val TAG = "ZzimViewModel"
class ZzimViewModel: ViewModel() {

    private val likeService = RetrofitUtil.likeService
    private val recentCocktailIdRepository = RecentCocktailIdRepository.getInstance()

    // 찜한 칵테일 리스트
    private val _likedList = MutableLiveData<List<CocktailListResponse>>()
    val likedList: LiveData<List<CocktailListResponse>> get() = _likedList

    // 최근 본 칵테일 리스트
    private val _recentViewedList = MutableLiveData<List<CocktailListResponse>>()
    val recentViewedList: LiveData<List<CocktailListResponse>> get() = _recentViewedList

    private val _recentCoctailId = MutableLiveData<List<Int>>()
    val recentCoctailId: LiveData<List<Int>>
        get() = _recentCoctailId

    init {
        getLikedRecentViewed()
        loadRecntCocktailId()
    }

    // 좋아요(찜) 한 칵테일 리스트와 최근 본 칵테일 가져오기.
    fun getLikedRecentViewed() {
        val currentRecentIds = _recentCoctailId.value ?: listOf()

        if (currentRecentIds.isEmpty()) return // 빈 리스트라면 API 호출 안 함

        viewModelScope.launch {
            try {
                val likeRequest = LikeRequest(currentRecentIds)

                val response = likeService.getLikedRecentViewed(likeRequest)

                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        if (_likedList.value != body.likedCocktails) {
                            _likedList.postValue(body.likedCocktails)
                        }
                        if (_recentViewedList.value != body.recentViewedCocktails) {
                            _recentViewedList.postValue(body.recentViewedCocktails)
                        }
                    }
                    Log.d(TAG, "Recent getLikedRecentViewed: ${_recentViewedList.value}")
                }
            } catch (e: Exception) {
                Log.d(TAG, "getLikedRecentViewed: ${e.message}")
            }
        }
    }


    fun loadRecntCocktailId(){
        viewModelScope.launch {
            _recentCoctailId.value = recentCocktailIdRepository.getAllCocktails()
        }
    }
}