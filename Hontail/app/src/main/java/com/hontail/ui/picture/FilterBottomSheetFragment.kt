package com.hontail.ui.picture

import android.os.Bundle
import android.view.View
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

        val filterPosition = arguments?.getInt("filter", 0) ?: 0

        binding.apply {

            constraintLayoutFilterBottomSheetZzim.visibility = View.GONE
            constraintLayoutFilterBottomSheetTime.visibility = View.GONE
            constraintLayoutFilterBottomSheetAlcoholContent.visibility = View.GONE
            constraintLayoutFilterBottomSheetBase.visibility = View.GONE

            when(filterPosition) {

                0 -> constraintLayoutFilterBottomSheetZzim.visibility = View.VISIBLE
                1 -> constraintLayoutFilterBottomSheetTime.visibility = View.VISIBLE
                2 -> constraintLayoutFilterBottomSheetAlcoholContent.visibility = View.VISIBLE
                3 -> constraintLayoutFilterBottomSheetBase.visibility = View.VISIBLE
            }
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

        binding.apply {
            textViewFilterSearch.setOnClickListener {
                dismiss()
            }
        }
    }

    companion object {
        fun newInstance(filter: Int): FilterBottomSheetFragment {
            val fragment = FilterBottomSheetFragment()
            val args = Bundle()
            args.putInt("filter", filter)
            fragment.arguments = args
            return fragment
        }
    }
}
