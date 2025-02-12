package com.hontail.ui.login

import android.util.Base64
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hontail.data.model.request.LoginRequest
import com.hontail.data.remote.RetrofitUtil
import kotlinx.coroutines.launch
import org.json.JSONObject

private const val TAG = "LoginFragmentViewModel_SSAFY"

class LoginFragmentViewModel : ViewModel() {

    private val _jwtToken = MutableLiveData<String>()
    val jwtToken: LiveData<String?> get() = _jwtToken

    private val _userId = MutableLiveData<String>()
    val userId: LiveData<String?> get() = _userId

    private val _userEmail = MutableLiveData<String>()
    val userEmail: LiveData<String?> get() = _userEmail

    fun loginWithNaver(accessToken: String) {
        viewModelScope.launch {
            runCatching {
                Log.d(TAG, "loginWithNaver: $accessToken")
                RetrofitUtil.loginService.socialLogin(LoginRequest(token = accessToken, provider = "Naver"))
            }.onSuccess { response ->
                Log.d(TAG, "loginWithNaver: ${response.code()} - ${response.message()}")
                if (response.isSuccessful) {
                    val jwt = response.body()?.jwt
                    _jwtToken.value = jwt!!  // JWT 저장
                    Log.d(TAG, "Login Success! JWT: ${_jwtToken.value}")

                    // JWT 디코딩 및 사용자 정보 저장
                    jwt?.let { decodeJwt(it) }

                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e(TAG, "Login Failed: $errorBody")
                    Log.e(TAG, "Response code: ${response.code()}, Message: ${response.message()}")
                }
            }.onFailure { throwable ->
                Log.e(TAG, "Network error: ${throwable.message}")
                throwable.printStackTrace()
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
                    val jwt = response.body()?.jwt
                    _jwtToken.value = jwt!!  // JWT 저장
                    Log.d(TAG, "Login Success! JWT: ${_jwtToken.value}")

                    // JWT 디코딩 및 사용자 정보 저장
                    jwt?.let { decodeJwt(it) }

                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e(TAG, "Login Failed: $errorBody")
                    Log.e(TAG, "Response code: ${response.code()}, Message: ${response.message()}")
                }
            }.onFailure { throwable ->
                Log.e(TAG, "Network error: ${throwable.message}")
                throwable.printStackTrace()
            }
        }
    }

    // 🔹 JWT 디코딩 메서드
    private fun decodeJwt(jwt: String) {
        try {
            val parts = jwt.split(".")
            if (parts.size < 2) {
                Log.e(TAG, "Invalid JWT format")
                return
            }

            val payload = parts[1]  // JWT의 두 번째 부분 (Payload)
            val decodedBytes = Base64.decode(payload, Base64.URL_SAFE)
            val decodedString = String(decodedBytes, Charsets.UTF_8)

            Log.d(TAG, "Decoded JWT Payload: $decodedString")

            // JSON 파싱
            val json = JSONObject(decodedString)
            _userId.value = json.optString("user_id", "Unknown")
            _userEmail.value = json.optString("email", "Unknown")

            Log.d(TAG, "Extracted UserId: ${_userId.value}, Email: ${_userEmail.value}")

        } catch (e: Exception) {
            Log.e(TAG, "Error decoding JWT: ${e.message}")
        }
    }
}
