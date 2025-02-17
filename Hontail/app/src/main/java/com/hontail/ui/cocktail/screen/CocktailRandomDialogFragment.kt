package com.hontail.ui.cocktail.screen

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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = CustomDialogShakeBinding.inflate(inflater, container, false)

        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initEvent()
    }

    override fun onStart() {
        super.onStart()

        initScreen()
        initSensor()
    }

    fun initScreen(){
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val window = dialog?.window
        val params = window?.attributes

        // 화면 너비에서 20dp 양쪽 margin을 제외한 크기 계산
        val marginInPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 20f, resources.displayMetrics
        ).toInt()
        params?.width = resources.displayMetrics.widthPixels - (marginInPx * 2)
        params?.height = ViewGroup.LayoutParams.WRAP_CONTENT

        // 설정된 LayoutParams를 다이얼로그에 적용
        window?.attributes = params
    }

    fun initSensor(){
        sensorManager?.registerListener(
            this,
            sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_UI
        )
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            // 가속도 값의 변화량을 계산
            val acceleration = sqrt((x * x + y * y + z * z).toDouble())

            // 일정 값(예: 15.0) 이상이면 흔들기 감지
            if (acceleration > 15) {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastShakeTime > 1000) { // 1초 이내 중복 감지 방지
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
        // 다이얼로그가 사라질 때 센서 해제
        sensorManager?.unregisterListener(this)
    }

    private fun onShakeDetected() {
        // 흔들기 감지 시 실행할 동작
        Log.d(TAG, "Recommendation onShakeDetected!!!!!!!")
        activityViewModel.getRecommendedCocktailId()
        sensorManager?.unregisterListener(this)
    }

    private fun initEvent() {
        binding.apply {

            imageViewCustomDialogShakeClose.setOnClickListener {
                dismiss()
            }
        }

        activityViewModel.cocktailId.observe(viewLifecycleOwner){
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