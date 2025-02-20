package com.hontail.data.remote

import retrofit2.http.POST
import retrofit2.http.Query

interface S3Service {

    // S3에 이미지 등록
    @POST("api/s3/presigned-url")
    suspend fun insertS3(
        @Query("fileName") fileName: String
    ): String
}