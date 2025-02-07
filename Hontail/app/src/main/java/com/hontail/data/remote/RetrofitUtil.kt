package com.hontail.data.remote

import com.hontail.base.ApplicationClass

class RetrofitUtil {
    companion object{
        val loginService = ApplicationClass.retrofit.create(LoginService::class.java)
    }
}