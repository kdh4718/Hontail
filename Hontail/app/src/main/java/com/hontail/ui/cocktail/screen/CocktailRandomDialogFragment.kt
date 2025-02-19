package com.hontail.ui.cocktail.screen

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.hontail.databinding.CustomDialogShakeBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.MainActivityViewModel
import com.hontail.util.CommonUtils
import kotlin.math.sqrt

private const val TAG = "CocktailRandomDialogFra_SSAFY"

class CocktailRandomDialogFragment(): DialogFragment(), SensorEventListener {
    private lateinit var mainActivity: MainActivity
    private lateinit var binding: CustomDialogShakeBinding
    private val activityViewModel: MainActivityViewModel by activityViewModels()

    private var sensorManager: SensorManager? = null
    private var lastShakeTime: Long = 0
    private var shakeAnimation: ObjectAnimator? = null
    private var isLoading = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = CustomDialogShakeBinding.inflate(inflater, container, false)
        dialog?.setCancelable(false)  // 로딩 중에는 취소 불가능하게 설정
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
    }

    private fun startShakeAnimation() {
        val phoneImage = binding.imageViewCustomDialogShakeShake

        // 회전 애니메이션 설정
        shakeAnimation = ObjectAnimator.ofFloat(phoneImage, "rotation", -15f, 15f).apply {
            duration = 500 // 한 번의 회전에 걸리는 시간 (밀리초)
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            interpolator = LinearInterpolator()
        }

        shakeAnimation?.start()
    }

    override fun onStart() {
        super.onStart()
        initScreen()
        initSensor()
    }

    private fun initScreen(){
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val window = dialog?.window
        val params = window?.attributes

        val marginInPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 20f, resources.displayMetrics
        ).toInt()
        params?.width = resources.displayMetrics.widthPixels - (marginInPx * 2)
        params?.height = ViewGroup.LayoutParams.WRAP_CONTENT

        window?.attributes = params
    }

    private fun initSensor(){
        sensorManager?.registerListener(
            this,
            sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_UI
        )
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (isLoading) return // 이미 로딩 중이면 추가 감지 무시

        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val acceleration = sqrt((x * x + y * y + z * z).toDouble())

            if (acceleration > 15) {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastShakeTime > 1000) {
                    lastShakeTime = currentTime
                    onShakeDetected()
                }
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    override fun onStop() {
        super.onStop()
        sensorManager?.unregisterListener(this)
        shakeAnimation?.cancel()
    }

    private fun onShakeDetected() {
        if (isLoading) return

        isLoading = true
        updateUIForLoading()
        startShakeAnimation()

        Log.d(TAG, "Recommendation onShakeDetected!!!!!!!")
        activityViewModel.getRecommendedCocktailId()
        sensorManager?.unregisterListener(this)
    }

    private fun updateUIForLoading() {
        binding.apply {
            // 텍스트 변경
            textViewCustomDialogShakeTop.text = "칵테일 검색중이에요!"
            textViewCustomDialogShakeBottom.text = "잠시만 기다려주세요"

            // Close 버튼 비활성화
            imageViewCustomDialogShakeClose.isEnabled = false
            imageViewCustomDialogShakeClose.alpha = 0.5f
        }
    }

    private fun initEvent() {
        binding.apply {
            imageViewCustomDialogShakeClose.setOnClickListener {
                if (!isLoading) {
                    dismiss()
                }
            }
        }

        activityViewModel.cocktailId.observe(viewLifecycleOwner) {
            changeFragment(it)
        }
    }

    private fun changeFragment(id: Int){
        Log.d(TAG, "initEvent: ${id}")
        if (id != 1){
            dismiss()
            mainActivity.changeFragment(CommonUtils.MainFragmentName.COCKTAIL_DETAIL_FRAGMENT)
        }
    }
}