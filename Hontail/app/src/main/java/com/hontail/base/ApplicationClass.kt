package com.hontail.base

import android.app.Application
import retrofit2.Retrofit
import android.Manifest
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hontail.R
import com.hontail.data.local.IngredientRepository
import com.hontail.data.local.SearchHistoryRepository
import com.hontail.data.local.SharedPreferencesUtil
import com.hontail.data.model.dto.IngredientsTable
import com.kakao.sdk.common.KakaoSdk
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

class ApplicationClass : Application() {

    val SERVER_URL = "https://i12d207.p.ssafy.io/"

//    val SERVER_URL = "http://192.168.100.193:9090/"

    override fun onCreate() {
        super.onCreate()

        sharedPreferencesUtil = SharedPreferencesUtil(applicationContext)
        // 앱이 처음 생성되는 순간, retrofit 인스턴스를 생성

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // 요청 및 응답 본문을 로그로 출력
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val jwtToken = sharedPreferencesUtil.getJwtToken()

                // JWT가 존재하면 Authorization 헤더 추가
                val requestBuilder = originalRequest.newBuilder()
                jwtToken?.let {
                    requestBuilder.addHeader("Authorization", "Bearer $it")
                }

                val newRequest = requestBuilder.build()
                chain.proceed(newRequest)
            }
            .readTimeout(5000, TimeUnit.MILLISECONDS)
            .connectTimeout(5000, TimeUnit.MILLISECONDS)
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(SERVER_URL)
            .addConverterFactory(ScalarsConverterFactory.create()) // 단순 문자열 응답 처리
            .addConverterFactory(GsonConverterFactory.create(gson)) // JSON 응답 처리
            .client(okHttpClient) // OkHttpClient 설정 적용
            .build()

        retrofit9091 = Retrofit.Builder()
            .baseUrl(SERVER_URL_9091)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        // KaKao SDK 초기화
//        val kakaoAppKey = getString(R.string.kakao_app_key)
//        KakaoSdk.init(this, kakaoAppKey)


        // 앱 처음 생성되는 순간 룸 디비 생성.
        IngredientRepository.initialize(this)
        IngredientRepository.getInstance().refreshIngredients()

        SearchHistoryRepository.initialize(this)
    }

    companion object{
        lateinit var sharedPreferencesUtil: SharedPreferencesUtil
        lateinit var retrofit: Retrofit
        lateinit var retrofit9091: Retrofit

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
        const val SERVER_URL_9091 = "http://i12d207.p.ssafy.io:9091/"
    }
}