package com.hontail.util

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.hontail.databinding.FragmentDialogToLoginBinding
import com.hontail.ui.LoginActivity
import com.hontail.ui.MainActivity

class DialogToLoginFragment: DialogFragment() {

    private lateinit var binding: FragmentDialogToLoginBinding

    private lateinit var mainActivity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentDialogToLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initEvent()
    }

    // 이벤트
    private fun initEvent() {

        binding.apply {

            // 취소 버튼
            buttonDialogToLoginCancel.setOnClickListener {
                dismiss()
            }

            // 확인 버튼
            buttonDialogToLoginConfirm.setOnClickListener {

                val intent = Intent(mainActivity, LoginActivity::class.java)
                startActivity(intent)

                mainActivity.finish()

                dismiss()
            }
        }
    }
}