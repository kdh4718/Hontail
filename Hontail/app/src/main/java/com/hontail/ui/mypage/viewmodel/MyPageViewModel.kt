package com.hontail.ui.mypage.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hontail.data.model.request.MyPageNicknameRequest
import com.hontail.data.model.response.CocktailListResponse
import com.hontail.data.model.response.ContentX
import com.hontail.data.model.response.MyPageInformationResponse
import com.hontail.data.remote.RetrofitUtil
import kotlinx.coroutines.launch

private const val TAG = "MyPageViewModel"
class MyPageViewModel: ViewModel() {

    private val myPageService = RetrofitUtil.myPageService

    private val _userInfo = MutableLiveData<MyPageInformationResponse>()
    val userInfo: LiveData<MyPageInformationResponse> get() = _userInfo

    private val _cocktailList = MutableLiveData<List<CocktailListResponse>>(emptyList())
    val cocktailList: LiveData<List<CocktailListResponse>> get() = _cocktailList

    private val _isOkay = MutableLiveData<Boolean?>()
    val isOkay: LiveData<Boolean?> get() = _isOkay

    init {
        getUserInformation()
        getUserCustomCocktails()
    }

    // 사용자 정보 가져오기.
    private fun getUserInformation() {
        viewModelScope.launch {
            try {
                Log.d(TAG, "getUserInformation: 함수 호출 성공")
                val response = myPageService.getUserInformation()

                if(response.isSuccessful) {
                    Log.d(TAG, "getUserInformation: 응답 성공 ${response.body()}")
                    response.body()?.let {
                        _userInfo.value = it
                    }
                }
                else {
                    Log.d(TAG, "getUserInformation: 사용자 정보를 불러오지 못했습니다.")
                }
            }
            catch (e: Exception) {
                Log.d(TAG, "getUserInformation: 서버 오류 ${e.message}")
            }
        }
    }

    // 사용자가 만든 칵테일 조회
    private fun getUserCustomCocktails() {
        viewModelScope.launch {
            try {
                Log.d(TAG, "getUserCustomCocktails: 함수 호출됨")
                val response = myPageService.getUserCustomCocktails(0, 20)

                Log.d(TAG, "getUserCustomCocktails: 서버 응답 -> ${response.raw()}") // HTTP 응답 확인

                if (response.isSuccessful) {
                    Log.d(TAG, "getUserCustomCocktails: 응답 성공 -> ${response.body()}") // JSON 확인

                    val cocktailList = response.body()?.content?.map { it.toCocktailListResponse() } ?: emptyList()

                    Log.d(TAG, "getUserCustomCocktails: 변환된 칵테일 리스트 -> $cocktailList")

                    _cocktailList.value = cocktailList
                } else {
                    Log.d(TAG, "getUserCustomCocktails: 응답 실패 -> ${response.errorBody()?.string()}")

                    _cocktailList.value = emptyList()
                }
            } catch (e: Exception) {
                Log.d(TAG, "getUserCustomCocktails: 서버 오류 -> ${e.message}")
            }
        }
    }


    private fun ContentX.toCocktailListResponse(): CocktailListResponse {
        return CocktailListResponse(
            id = this.id,
            cocktailName = this.cocktailName,
            imageUrl = this.imageUrl,
            likes = this.likesCnt,  // 이름이 다름 (likesCnt → likes)
            alcoholContent = this.alcoholContent,
            baseSpirit = this.baseSpirit,
            createdAt = this.createdAt,
            ingredientCount = this.ingredientCount,
            isLiked = this.isLiked
        )
    }

    // 닉네임 변경
    fun modifyUserNickname(newNickname: String) {

        viewModelScope.launch {

            try {

                Log.d(TAG, "modifyUserNickname: 닉네임 변경 요청 -> $newNickname")

                val request =MyPageNicknameRequest(newNickname)
                val response = myPageService.modifyUserNickname(request)

                if(response.isSuccessful) {

                    Log.d(TAG, "modifyUserNickname: 닉네임 변경 성공 -> ${response.body()}")

                    response.body()?.let {
                        _userInfo.value = it
                        _isOkay.value = true
                    }
                }
                else {
                    Log.d(TAG, "modifyUserNickname: 닉네임 변경 실패 -> ${response.errorBody()}")
                    _isOkay.value = false
                }
            }
            catch (e: Exception) {
                Log.d(TAG, "modifyUserNickname: 서버 오류 -> ${e.message}")
                _isOkay.value = false
            }
        }
    }
}