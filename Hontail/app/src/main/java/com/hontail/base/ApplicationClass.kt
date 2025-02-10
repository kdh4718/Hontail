package com.hontail.base

import android.app.Application
import retrofit2.Retrofit
import android.Manifest
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hontail.R
import com.hontail.data.local.SharedPreferencesUtil
import com.kakao.sdk.common.KakaoSdk
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApplicationClass : Application() {

    val SERVER_URL = "http://i12d207.p.ssafy.io:9090/"

//    val SERVER_URL = "http://192.168.100.72:8080/"

    override fun onCreate() {
        super.onCreate()

        KakaoSdk.init(this, R.string.kakao_app_key.toString())

        sharedPreferencesUtil = SharedPreferencesUtil(applicationContext)
        // 앱이 처음 생성되는 순간, retrofit 인스턴스를 생성

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // 요청 및 응답 본문을 로그로 출력
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .readTimeout(5000, TimeUnit.MILLISECONDS) // 읽기 시간 초과
            .connectTimeout(5000, TimeUnit.MILLISECONDS) // 연결 시간 초과
            .build()


        retrofit = Retrofit.Builder()
            .baseUrl(SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient) // OkHttpClient 설정 적용
            .build()
    }

    companion object{
        lateinit var sharedPreferencesUtil: SharedPreferencesUtil
        lateinit var retrofit: Retrofit

        val gson : Gson = GsonBuilder()
            .setLenient()
            .create()

        // 모든 퍼미션 관련 배열
        val requiredPermissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.BLUETOOTH_CONNECT
        )
    }
}