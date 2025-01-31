package com.hontail.ui.picture

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hontail.R
import com.hontail.base.BaseBottomSheetFragment
import com.hontail.databinding.FragmentFilterBottomSheetBinding

class FilterBottomSheetFragment : BaseBottomSheetFragment<FragmentFilterBottomSheetBinding>(
    FragmentFilterBottomSheetBinding::bind,
    R.layout.fragment_filter_bottom_sheet
){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initRadio()
    }

    fun initView(){
        // 전달된 값 확인 (기본값 false)
        val shouldHideBase = arguments?.getBoolean("hideBase", false) ?: false

        if (shouldHideBase) {
            binding?.frameLayoutFilterBase!!.visibility = View.GONE
        } else {
            binding?.frameLayoutFilterBase!!.visibility = View.VISIBLE
        }
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

    companion object {
        fun newInstance(hideBase: Boolean): FilterBottomSheetFragment {
            val fragment = FilterBottomSheetFragment()
            val args = Bundle()
            args.putBoolean("hideBase", hideBase)
            fragment.arguments = args
            return fragment
        }
    }
}
