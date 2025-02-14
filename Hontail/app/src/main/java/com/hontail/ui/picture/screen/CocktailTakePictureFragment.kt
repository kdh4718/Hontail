// CocktailTakePictureFragment.kt
package com.hontail.ui.picture.screen

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
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
import com.hontail.ui.picture.VisionService
import com.hontail.util.CommonUtils
import java.io.ByteArrayOutputStream
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.TranslateAnimation
import androidx.fragment.app.DialogFragment
import com.hontail.databinding.CustomDialogPhotoLoadingBinding

private const val TAG = "CocktailTakePictureFrag_SSAFY"

class CocktailTakePictureFragment : BaseFragment<FragmentCocktailTakePictureBinding>(
    FragmentCocktailTakePictureBinding::bind,
    R.layout.fragment_cocktail_take_picture
) {
    private lateinit var mainActivity: MainActivity
    private val activityViewModel: MainActivityViewModel by activityViewModels()

    private var imageCapture: ImageCapture? = null
    private lateinit var visionService: VisionService
    private lateinit var loadingDialog: LoadingDialogFragment
    private var isAnalyzing = false

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

    fun initEvent() {
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

    private fun showLoadingDialog() {
        loadingDialog = LoadingDialogFragment()
        loadingDialog.show(childFragmentManager, "LoadingDialog")
        isAnalyzing = true
    }

    private fun openCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.cameraXTakePicturecamera.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
            } catch (exc: Exception) {
                Log.e("Camera", "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto() {
        if (isAnalyzing) return  // 이미 분석 중이면 중복 실행 방지

        showLoadingDialog() // 먼저 다이얼로그 표시

        imageCapture?.takePicture(
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)

                    val bitmap = imageProxyToBitmap(image)
                    processImage(bitmap)

                    image.close()
                }

                override fun onError(exception: ImageCaptureException) {
                    super.onError(exception)
                    Log.e(TAG, "Photo capture failed: ${exception.message}", exception)
                    if (::loadingDialog.isInitialized && loadingDialog.isAdded) {
                        loadingDialog.dismissAllowingStateLoss()
                    }
                    isAnalyzing = false
                }
            })
    }

    private fun processImage(bitmap: Bitmap) {
        try {
            if (!isAdded) return

            val resizedBitmap = resizeBitmap(bitmap, 800, 800)
            val stream = ByteArrayOutputStream()
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
            val byteArray = stream.toByteArray()

            byteArray?.let {
                try {
                    val detectedText = visionService.detectTextAndLabelsFromImage(bitmap)

                    Handler(Looper.getMainLooper()).postDelayed({
                        try {
                            if (!isAdded) return@postDelayed

                            if (::loadingDialog.isInitialized && loadingDialog.isAdded) {
                                loadingDialog.dismissAllowingStateLoss()
                            }
                            isAnalyzing = false

                            Log.d(TAG, "Vision API: ${detectedText}")
                            activityViewModel.setIngredientList(detectedText)
                            mainActivity.changeFragment(CommonUtils.MainFragmentName.COCKTAIL_PICTURE_RESULT_FRAGMENT)
                        } catch (e: Exception) {
                            Log.e(TAG, "Error during dialog dismiss", e)
                        }
                    }, 5000)
                } catch (e: Exception) {
                    Log.e(TAG, "Error during image processing", e)
                    if (::loadingDialog.isInitialized && loadingDialog.isAdded) {
                        loadingDialog.dismissAllowingStateLoss()
                    }
                    isAnalyzing = false
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error during image processing", e)
            if (::loadingDialog.isInitialized && loadingDialog.isAdded) {
                loadingDialog.dismissAllowingStateLoss()
            }
            isAnalyzing = false
        }
    }

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

// LoadingDialogFragment.kt
class LoadingDialogFragment : DialogFragment() {
    private var _binding: CustomDialogPhotoLoadingBinding? = null
    private val binding get() = _binding!!

    private val dummyIngredients = listOf(
        "위스키 분석중...",
        "보드카 확인중...",
        "진 검색중...",
        "럼 스캔중...",
        "데킬라 분석중...",
        "리큐어 확인중..."
    )

    private var currentIndex = 0
    private lateinit var updateTextHandler: Handler
    private lateinit var updateTextRunnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.TransparentDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CustomDialogPhotoLoadingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        setupAnimation()
        initializeUpdateText()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        try {
            updateTextHandler.removeCallbacks(updateTextRunnable)
        } catch (e: Exception) {
            Log.e("LoadingDialog", "Error removing callbacks", e)
        }
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            if (::updateTextHandler.isInitialized) {
                updateTextHandler.removeCallbacks(updateTextRunnable)
            }
        } catch (e: Exception) {
            Log.e("LoadingDialog", "Error during destroy", e)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setCanceledOnTouchOutside(false)
            setCancelable(false)
        }
    }

    private fun setupAnimation() {
        val slideUp = AnimationSet(true).apply {
            addAnimation(AlphaAnimation(1f, 0f).apply {
                duration = 500
                fillAfter = true
            })
            addAnimation(TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, -1f
            ).apply {
                duration = 500
                fillAfter = true
            })
        }

        val slideIn = AnimationSet(true).apply {
            addAnimation(AlphaAnimation(0f, 1f).apply {
                duration = 500
                fillAfter = true
            })
            addAnimation(TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 1f,
                Animation.RELATIVE_TO_SELF, 0f
            ).apply {
                duration = 500
                fillAfter = true
            })
        }

        binding.textViewCustomDialogIngredientBottom.animation = slideUp
    }

    private fun initializeUpdateText() {
        updateTextHandler = Handler(Looper.getMainLooper())
        updateTextRunnable = object : Runnable {
            override fun run() {
                updateLoadingText()
                updateTextHandler.postDelayed(this, 1000)
            }
        }
        updateTextHandler.post(updateTextRunnable)
    }

    private fun updateLoadingText() {
        val currentText = dummyIngredients[currentIndex]

        val slideUp = AnimationSet(true).apply {
            addAnimation(AlphaAnimation(1f, 0f).apply {
                duration = 500
                fillAfter = true
            })
            addAnimation(TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, -1f
            ).apply {
                duration = 500
                fillAfter = true
            })
        }

        val slideIn = AnimationSet(true).apply {
            addAnimation(AlphaAnimation(0f, 1f).apply {
                duration = 500
                fillAfter = true
            })
            addAnimation(TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 1f,
                Animation.RELATIVE_TO_SELF, 0f
            ).apply {
                duration = 500
                fillAfter = true
            })
        }

        binding.textViewCustomDialogIngredientBottom.startAnimation(slideUp)

        slideUp.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                binding.textViewCustomDialogIngredientBottom.text = currentText
                binding.textViewCustomDialogIngredientBottom.startAnimation(slideIn)
            }
        })

        currentIndex = (currentIndex + 1) % dummyIngredients.size
    }

    companion object {
        fun newInstance() = LoadingDialogFragment()
    }
}