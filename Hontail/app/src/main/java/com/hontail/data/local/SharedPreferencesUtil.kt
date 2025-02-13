package com.hontail.data.local

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

class SharedPreferencesUtil(context: Context) {
    val SHARED_PREFERENCES_NAME = "hontail_preference"
    val COOKIES_KEY_NAME = "cookies"

    var preferences: SharedPreferences =
        context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    private val prefs: SharedPreferences =
        context.getSharedPreferences("hontail_prefs", Context.MODE_PRIVATE)

    companion object {
//        private const val KEY_JWT = "jwt_token"

        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_ACCESS_TOKEN = "access_token"
    }

    // Token들 저장
    fun saveTokens(refreshToken: String, accessToken: String) {
        prefs.edit().apply {
            putString(KEY_REFRESH_TOKEN, refreshToken)
            putString(KEY_ACCESS_TOKEN, accessToken)
            apply()
        }
    }

    // RefreshToken 가져오기.
    fun getRefreshToken(): String? {
        return prefs.getString(KEY_REFRESH_TOKEN, null)
    }

    // AccessToken 가져오기.
    fun getAccessToken(): String? {
        return prefs.getString(KEY_ACCESS_TOKEN, null)
    }

    // 토큰 삭제
    fun clearTokens() {
        prefs.edit().apply {
            remove(KEY_REFRESH_TOKEN)
            remove(KEY_ACCESS_TOKEN)
            apply()
        }
    }

//    fun saveJwtToken(token: String) {
//        prefs.edit().putString(KEY_JWT, token).apply()
//    }
//
//    fun getJwtToken(): String? {
//        return prefs.getString(KEY_JWT, null)
//    }

    fun addUserCookie(cookies: HashSet<String>) {
        val editor = preferences.edit()
        editor.putStringSet(COOKIES_KEY_NAME, cookies)
        editor.apply()
    }

    fun getUserCookie(): MutableSet<String>? {
        return preferences.getStringSet(COOKIES_KEY_NAME, HashSet())
    }

    fun deleteUserCookie() {
        preferences.edit().remove(COOKIES_KEY_NAME).apply()
    }

    fun addNotice(info: String) {
        val list = getNotice()

        list.add(info)
        val json = Gson().toJson(list)

        preferences.edit().let {
            it.putString("notice", json)
            it.apply()
        }
    }

    fun setNotice(list: MutableList<String>) {
        preferences.edit().let {
            it.putString("notice", Gson().toJson(list))
            it.apply()
        }
    }

    fun getNotice(): MutableList<String> {
        val str = preferences.getString("notice", "")!!
        val list = if (str.isEmpty()) mutableListOf<String>() else Gson().fromJson(str, MutableList::class.java) as MutableList<String>
        return list
    }
}