package com.hontail.ui.bartender.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hontail.data.model.response.BartenderResponse
import com.hontail.data.model.response.Cocktail
import com.hontail.data.remote.RetrofitUtil
import com.hontail.ui.bartender.screen.ChatMessage
import kotlinx.coroutines.launch
import retrofit2.Response

class BartenderViewModel: ViewModel() {

    private val bartenderService = RetrofitUtil.bartenderService

    private val _messages = MutableLiveData<List<ChatMessage>>()
    val messages: LiveData<List<ChatMessage>> get() = _messages

    private val _recommendedCocktail = MutableLiveData<Cocktail?>()
    val recommendedCocktail: LiveData<Cocktail?> get() = _recommendedCocktail

    init {
        receiveBartenderGreeting()
    }

    // 바텐더의 첫 인삿말을 가져옴
    private fun receiveBartenderGreeting() {
        viewModelScope.launch {
            try {
                val response: Response<BartenderResponse> = bartenderService.receiveFromBartender()
                if (response.isSuccessful && response.body() != null) {
                    val bartenderResponse = response.body()!!

                    // 바텐더 메시지 추가
                    val newMessage = ChatMessage(bartenderResponse.message, false, getCurrentTime())
                    updateMessages(newMessage)

                    // 칵테일 추천이 있는 경우 저장
                    if (bartenderResponse.cocktailRecommendation) {
                        _recommendedCocktail.postValue(bartenderResponse.cocktail)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // 사용자 메시지 추가
    fun addUserMessage(text: String) {
        val userMessage = ChatMessage(text, true, getCurrentTime())
        updateMessages(userMessage)
    }

    // 바텐더 메시지 추가
    fun addBartenderMessage(response: BartenderResponse) {
        val bartenderMessage = ChatMessage(response.message, false, getCurrentTime())
        updateMessages(bartenderMessage)

        // 칵테일 추천이 있는 경우 업데이트
        if (response.cocktailRecommendation) {
            _recommendedCocktail.postValue(response.cocktail)
        }
    }

    // 메시지 리스트 업데이트
    private fun updateMessages(newMessage: ChatMessage) {
        val currentList = _messages.value?.toMutableList() ?: mutableListOf()
        currentList.add(newMessage)
        _messages.postValue(currentList)
    }

    // 현재 시간 가져오기
    private fun getCurrentTime(): String {
        val sdf = java.text.SimpleDateFormat("aa hh:mm", java.util.Locale.getDefault())
        return sdf.format(java.util.Date())
    }
}