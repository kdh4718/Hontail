package com.hontail.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hontail.data.model.request.LoginRequest
import com.hontail.data.remote.RetrofitUtil
import kotlinx.coroutines.launch

private const val TAG = "LoginFragmentViewModel"

class LoginFragmentViewModel : ViewModel() {

    private val _jwtToken = MutableLiveData<String>()
    val jwtToken: LiveData<String?> get() = _jwtToken

    fun loginWithNaver(accessToken: String) {
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.loginService.socialLogin(LoginRequest(token = accessToken, provider = "naver"))
            }.onSuccess { response ->
                Log.d(TAG, "loginWithNaver: ${response.code()}")
                if (response.isSuccessful) {
                    _jwtToken.value = response.body()?.jwt  // JWT 저장
                    Log.d(TAG, "Login Success! JWT: ${_jwtToken.value}")
                } else {
                    Log.e(TAG, "Login Failed: ${response.errorBody()?.string()}")
                }
            }.onFailure { throwable ->
                Log.e(TAG, "Network error: ${throwable.message}")
            }
        }
    }
}
