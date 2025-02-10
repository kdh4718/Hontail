package com.hontail.ui.picture

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hontail.R
import com.hontail.base.BaseBottomSheetFragment
import com.hontail.databinding.FragmentFilterBottomSheetBinding
import com.hontail.ui.MainActivityViewModel

class FilterBottomSheetFragment : BaseBottomSheetFragment<FragmentFilterBottomSheetBinding>(
    FragmentFilterBottomSheetBinding::bind,
    R.layout.fragment_filter_bottom_sheet
){
    private val viewModel: MainActivityViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initRadio()
        setupObservers()
    }

    private fun setupObservers() {
        // 저장된 필터 상태 복원
        viewModel.selectedZzimFilter.observe(viewLifecycleOwner) { radioButtonId ->
            radioButtonId?.let {
                binding.radioGroupFilterZzim.check(it)
            }
        }

        viewModel.selectedTimeFilter.observe(viewLifecycleOwner) { radioButtonId ->
            radioButtonId?.let {
                binding.radioGroupFilterTime.check(it)
            }
        }

        viewModel.selectedAlcoholFilter.observe(viewLifecycleOwner) { radioButtonId ->
            radioButtonId?.let {
                binding.radioGroupFilterAlcoholContent.check(it)
            }
        }

        viewModel.selectedBaseFilter.observe(viewLifecycleOwner) { radioButtonId ->
            radioButtonId?.let {
                // 기존에 선택된 베이스 필터 복원
                radioButtons.find { it.id == radioButtonId }?.isChecked = true
            }
        }
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

    private val radioButtons by lazy {
        listOf(
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
    }

    fun initRadio() {
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
            binding.apply {
                textViewFilterSearch.setOnClickListener {
                    // 찜 필터
                    radioGroupFilterZzim.checkedRadioButtonId.let {
                        if (it != -1) {
                            viewModel.setZzimFilter(it)
                            viewModel.updateZzimButtonState(true)
                        }
                    }

                    // 시간 필터
                    radioGroupFilterTime.checkedRadioButtonId.let {
                        if (it != -1) {
                            viewModel.setTimeFilter(it)
                            viewModel.updateTimeButtonState(true)
                        }
                    }

                    // 도수 필터
                    radioGroupFilterAlcoholContent.checkedRadioButtonId.let {
                        if (it != -1) {
                            viewModel.setAlcoholFilter(it)
                            viewModel.updateAlcoholButtonState(true)
                        }
                    }

                    // 베이스주 필터
                    radioButtons.find { it.isChecked }?.let {
                        viewModel.setBaseFilter(it.id)
                        viewModel.updateBaseButtonState(true)
                    }

                    dismiss()
                }
            }

        }

        binding.apply {
            textViewFilterSearch.setOnClickListener {
                // 현재 선택된 필터들 저장
                radioGroupFilterZzim.checkedRadioButtonId.let {
                    if (it != -1) viewModel.setZzimFilter(it)
                }
                radioGroupFilterTime.checkedRadioButtonId.let {
                    if (it != -1) viewModel.setTimeFilter(it)
                }
                radioGroupFilterAlcoholContent.checkedRadioButtonId.let {
                    if (it != -1) viewModel.setAlcoholFilter(it)
                }
                // Base 필터의 경우 선택된 RadioButton ID 저장
                radioButtons.find { it.isChecked }?.let {
                    viewModel.setBaseFilter(it.id)
                }
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