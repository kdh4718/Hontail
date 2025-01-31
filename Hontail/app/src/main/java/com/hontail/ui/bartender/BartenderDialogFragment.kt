package com.hontail.ui.bartender

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.hontail.databinding.BartenderDialogBinding
import com.hontail.util.CommonUtils

class BartenderDialogFragment(private var mode: CommonUtils.BartenderRecordMode): DialogFragment() {

    private lateinit var binding: BartenderDialogBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = BartenderDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUI(mode)
        initEvent()
    }

    override fun onStart() {
        super.onStart()

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

    // 이벤트
    private fun initEvent() {
        binding.apply {

            // 녹음 시작
            imageViewDialogBartenderStart.setOnClickListener {
                changeMode(CommonUtils.BartenderRecordMode.RECORDING)

                // 녹음 기능 추가하기...
            }

            // 녹음 중단
            imageViewDialogBartenderStop.setOnClickListener {
                changeMode(CommonUtils.BartenderRecordMode.COMPLETED)

                // 녹음 중지 기능 추가하기...
            }

            // 녹음 다시
            imageViewDialogBartenderAgain.setOnClickListener {
                changeMode(CommonUtils.BartenderRecordMode.RECORDING)

                // 녹음 기능 추가하기...
            }

            // 녹음 전송
            imageViewDialogBartenderSend.setOnClickListener {
                // 녹음 파일 전송하기...

                dismiss()
            }
        }
    }

    // 모드 변경
    private fun updateUI(mode: CommonUtils.BartenderRecordMode) {
        binding.apply {
            when(mode) {

                // 음성 녹음 준비 모드
                CommonUtils.BartenderRecordMode.READY -> {
                    textViewDialogBartenderTitle.text = "말씀해주세요."
                    imageViewDialogBartenderStart.visibility = View.VISIBLE
                    imageViewDialogBartenderStop.visibility = View.GONE
                    imageViewDialogBartenderAgain.visibility = View.GONE
                    imageViewDialogBartenderSend.visibility = View.GONE
                }

                // 음성 녹음 중 모드
                CommonUtils.BartenderRecordMode.RECORDING -> {
                    textViewDialogBartenderTitle.text = "듣고 있습니다."
                    imageViewDialogBartenderStart.visibility = View.GONE
                    imageViewDialogBartenderStop.visibility = View.VISIBLE
                    imageViewDialogBartenderAgain.visibility = View.GONE
                    imageViewDialogBartenderSend.visibility = View.VISIBLE
                }

                // 음성 녹음 완료 모드
                CommonUtils.BartenderRecordMode.COMPLETED -> {
                    textViewDialogBartenderTitle.text = "다 들었습니다."
                    imageViewDialogBartenderStart.visibility = View.GONE
                    imageViewDialogBartenderStop.visibility = View.GONE
                    imageViewDialogBartenderAgain.visibility = View.VISIBLE
                    imageViewDialogBartenderSend.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun changeMode(newMode: CommonUtils.BartenderRecordMode) {
        mode = newMode
        updateUI(mode)
    }
}