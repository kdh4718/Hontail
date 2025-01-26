package com.hontail.ui.picture

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hontail.R
import com.hontail.databinding.FragmentFilterBottomSheetBinding

class FilterBottomSheetFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentFilterBottomSheetBinding? = null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFilterBottomSheetBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRadio()
    }

    fun initRadio() {
        binding?.let { binding ->
            // 라디오 버튼 리스트
            val radioButtons = listOf(
                binding.radioButtonFilterBaseJin,
                binding.radioButtonFilterBaseRum,
                binding.radioButtonFilterBaseVodka,
                binding.radioButtonFilterBaseWhiskey,
                binding.radioButtonFilterBaseTequila,
                binding.radioButtonFilterBaseLiqueur,
                binding.radioButtonFilterBaseWine,
                binding.radioButtonFilterBaseBrandy,
                binding.radioButtonFilterBaseEtc
            )

            // 각 버튼 클릭 리스너 설정
            radioButtons.forEach { radioButton ->
                radioButton.setOnClickListener {
                    // 클릭된 버튼을 제외한 나머지 버튼 선택 해제
                    radioButtons.forEach { otherButton ->
                        if (otherButton != radioButton) {
                            otherButton.isChecked = false
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null  // 메모리 누수 방지를 위한 바인딩 객체 해제
    }
}
