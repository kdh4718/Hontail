package com.hontail.data.remote

import com.hontail.base.ApplicationClass
import com.hontail.base.ApplicationClass.Companion.retrofit
import retrofit2.create

class RetrofitUtil {
    companion object{
        val loginService = ApplicationClass.retrofit.create(LoginService::class.java)
    }
}