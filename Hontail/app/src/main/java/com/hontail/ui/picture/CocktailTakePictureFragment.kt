package com.hontail.ui.picture

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.hontail.R
import com.hontail.base.BaseFragment
import com.hontail.databinding.FragmentCocktailTakePictureBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.MainActivityViewModel
import com.hontail.util.CommonUtils
import java.io.ByteArrayOutputStream

private const val TAG = "CocktailTakePictureFrag_SSAFY"

class CocktailTakePictureFragment : BaseFragment<FragmentCocktailTakePictureBinding>(
    FragmentCocktailTakePictureBinding::bind,
    R.layout.fragment_cocktail_take_picture
) {
    private lateinit var mainActivity: MainActivity
    private val activityViewModel: MainActivityViewModel by activityViewModels()

    private var imageCapture: ImageCapture? = null
    private lateinit var visionService: VisionService

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity.hideBottomNav(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        visionService = VisionService(requireContext())

        openCamera()
        initEvent()
    }

    fun initEvent(){
        binding.apply {
            imageViewTakePictureCancel.setOnClickListener {
                mainActivity.hideBottomNav(false)
                mainActivity.changeFragment(CommonUtils.MainFragmentName.HOME_FRAGMENT)
            }

            imageViewTakePictureExplanationCancel.setOnClickListener {
                cardViewTakePictureExplanation.visibility = View.GONE
            }

            imageViewTakePictureTakePicture.setOnClickListener {
                takePhoto()
            }
        }
    }

    private fun openCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // 미리보기 설정
            val preview = Preview.Builder()
                .build()
                .also {
                    // SurfaceProvider 설정
                    it.setSurfaceProvider(binding.cameraXTakePicturecamera.surfaceProvider)
                }

            // 이미지 캡처 설정
            imageCapture = ImageCapture.Builder()
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()  // 이전 바인딩 해제
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
            } catch (exc: Exception) {
                Log.e("Camera", "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto() {
        imageCapture?.takePicture(
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)

                    val bitmap = imageProxyToBitmap(image)
                    imageAdjustment(bitmap)

                    image.close()
                }
            })
    }

    private fun imageAdjustment(bitmap: Bitmap) {
        val resizedBitmap = resizeBitmap(bitmap, 800, 800) // 최대 크기 800x800으로 리사이즈
        val stream = ByteArrayOutputStream()
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream) // 압축 비율 설정
        val byteArray = stream.toByteArray()

        byteArray?.let {
            val detectedText = visionService.detectTextAndLabelsFromImage(bitmap)
            Log.d(TAG, "Vision API: ${detectedText}")
            activityViewModel.setIngredientList(detectedText)

            mainActivity.changeFragment(CommonUtils.MainFragmentName.COCKTAIL_PICTURE_RESULT_FRAGMENT)
        }
    }

    // 이미지 크기를 리사이즈하는 함수
    private fun resizeBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val ratio = width.toFloat() / height.toFloat()

        val newWidth = if (width > height) maxWidth else (maxHeight * ratio).toInt()
        val newHeight = if (height > width) maxHeight else (maxWidth / ratio).toInt()

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    private fun imageProxyToBitmap(image: ImageProxy): Bitmap {
        val buffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    override fun onDestroy() {
        super.onDestroy()
        mainActivity.hideBottomNav(false)
    }
}