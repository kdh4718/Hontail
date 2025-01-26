package com.hontail.ui.picture

import android.content.Context
import android.graphics.Bitmap
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.vision.v1.AnnotateImageRequest
import com.google.cloud.vision.v1.Feature
import com.google.cloud.vision.v1.Image
import com.google.cloud.vision.v1.ImageAnnotatorClient
import com.google.cloud.vision.v1.ImageAnnotatorSettings
import com.google.protobuf.ByteString
import java.io.ByteArrayOutputStream

class VisionService(private val context: Context) {

    // 이미지에서 텍스트 및 라벨을 감지하는 함수
    fun detectTextAndLabelsFromImage(bitmap: Bitmap): Pair<String, List<String>> {
        // Vision API 클라이언트 생성
        val visionClient = getVisionClient()

        // Bitmap을 ByteString으로 변환
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val imageBytes = ByteString.copyFrom(stream.toByteArray())

        // 이미지 객체 생성
        val image = Image.newBuilder().setContent(imageBytes).build()

        // 텍스트 감지 및 라벨 감지 기능 설정
        val textFeature = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build()
        val labelFeature = Feature.newBuilder().setType(Feature.Type.LABEL_DETECTION).build()

        // 요청 객체 생성
        val request = AnnotateImageRequest.newBuilder()
            .addFeatures(textFeature)
            .addFeatures(labelFeature)
            .setImage(image)
            .build()

        // Vision API를 통해 이미지 분석 요청 및 응답 처리
        val response = visionClient.batchAnnotateImages(listOf(request)).responsesList.first()

        // 텍스트 감지 결과 추출
        val detectedText = response.textAnnotationsList.firstOrNull()?.description ?: "No text detected"

        // 라벨 감지 결과 추출 (최대 5개 라벨)
        val detectedLabels = response.labelAnnotationsList.map { it.description }.take(5)

        return Pair(detectedText, detectedLabels)
    }

    // Vision API 클라이언트를 생성하는 함수
    private fun getVisionClient(): ImageAnnotatorClient {
        val credentials = GoogleCredentials.fromStream(context.assets.open("gcloud-key.json"))
        val settings = ImageAnnotatorSettings.newBuilder().setCredentialsProvider { credentials }.build()
        return ImageAnnotatorClient.create(settings)
    }
}