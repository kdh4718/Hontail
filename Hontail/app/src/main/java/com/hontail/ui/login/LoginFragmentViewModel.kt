package com.hontail.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hontail.data.model.request.LoginRequest
import com.hontail.data.remote.RetrofitUtil
import kotlinx.coroutines.launch

private const val TAG = "LoginFragmentViewModel_SSAFY"

class LoginFragmentViewModel : ViewModel() {

    private val _jwtToken = MutableLiveData<String>()
    val jwtToken: LiveData<String?> get() = _jwtToken

    fun loginWithNaver(accessToken: String) {
        viewModelScope.launch {
            runCatching {
                Log.d(TAG, "loginWithNaver: $accessToken")
                RetrofitUtil.loginService.socialLogin(LoginRequest(token = accessToken, provider = "Naver"))
            }.onSuccess { response ->
                Log.d(TAG, "loginWithNaver: ${response.code()} - ${response.message()}")
                if (response.isSuccessful) {
                    _jwtToken.value = response.body()?.jwt  // JWT 저장
                    Log.d(TAG, "Login Success! JWT: ${_jwtToken.value}")
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e(TAG, "Login Failed: $errorBody")
                    // 추가로 HTTP 상태 코드에 대한 로깅
                    Log.e(TAG, "Response code: ${response.code()}, Message: ${response.message()}")
                }
            }.onFailure { throwable ->
                Log.e(TAG, "Network error: ${throwable.message}")
                throwable.printStackTrace() // 예외 스택 트레이스를 로깅하여 좀 더 자세한 에러 원인 파악
            }
        }
    }

    fun loginWithKakao(accessToken: String) {
        viewModelScope.launch {
            runCatching {
                Log.d(TAG, "loginWithKakao: $accessToken")
                RetrofitUtil.loginService.socialLogin(LoginRequest(token = accessToken, provider = "Kakao"))
            }.onSuccess { response ->
                Log.d(TAG, "loginWithKakao: ${response.code()} - ${response.message()}")
                if (response.isSuccessful) {
                    _jwtToken.value = response.body()?.jwt  // JWT 저장
                    Log.d(TAG, "Login Success! JWT: ${_jwtToken.value}")
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e(TAG, "Login Failed: $errorBody")
                    Log.e(TAG, "Response code: ${response.code()}, Message: ${response.message()}")
                }
            }.onFailure { throwable ->
                Log.e(TAG, "Network error: ${throwable.message}")
                throwable.printStackTrace() // 예외 스택 트레이스를 로깅하여 좀 더 자세한 에러 원인 파악
            }
        }
    }

}
