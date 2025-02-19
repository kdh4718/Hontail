package com.hontail.ui.cocktail.screen

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.activityViewModels
import com.hontail.R
import com.hontail.base.BaseBottomSheetFragment
import com.hontail.databinding.FragmentFilterBottomSheetBinding
import com.hontail.ui.MainActivityViewModel

private const val TAG = "FilterBottomSheetFragme_SSAFY"

class FilterBottomSheetFragment : BaseBottomSheetFragment<FragmentFilterBottomSheetBinding>(
    FragmentFilterBottomSheetBinding::bind,
    R.layout.fragment_filter_bottom_sheet
) {
    private val activityViewModel: MainActivityViewModel by activityViewModels()
    private var tempSelectedBase: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initRadioButtons()
    }

    private val radioGroups: List<RadioGroup> by lazy {
        listOf(
            binding.radioGroupFilterZzim,
            binding.radioGroupFilterTime,
            binding.radioGroupFilterAlcoholContent
        )
    }

    private val gridRadioButtons: List<RadioButton> by lazy {
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

    private fun initView() {
        val filterPosition = arguments?.getInt("filter", 0) ?: 0

        binding.apply {
            // 모든 필터 레이아웃 숨기기
            listOf(
                constraintLayoutFilterBottomSheetZzim,
                constraintLayoutFilterBottomSheetTime,
                constraintLayoutFilterBottomSheetAlcoholContent,
                constraintLayoutFilterBottomSheetBase
            ).forEach { it.visibility = View.GONE }

            // 선택된 필터만 표시하고 상태 복원
            when (filterPosition) {
                0 -> {
                    constraintLayoutFilterBottomSheetZzim.visibility = View.VISIBLE
                    // 찜 필터가 선택된 상태라면 해당하는 라디오버튼 체크
                    if (activityViewModel.zzimButtonSelected) {
                        activityViewModel.selectedZzimFilter.value?.let { value ->
                            val radioButtonId = zzimFilterMap.entries.find { it.value == value }?.key
                            radioButtonId?.let { radioGroupFilterZzim.check(it) }
                        }
                    }
                }
                1 -> {
                    constraintLayoutFilterBottomSheetTime.visibility = View.VISIBLE
                    // 시간 필터가 선택된 상태라면 해당하는 라디오버튼 체크
                    if (activityViewModel.timeButtonSelected) {
                        activityViewModel.selectedTimeFilter.value?.let { value ->
                            val radioButtonId = timeFilterMap.entries.find { it.value == value }?.key
                            radioButtonId?.let { radioGroupFilterTime.check(it) }
                        }
                    }
                }
                2 -> {
                    constraintLayoutFilterBottomSheetAlcoholContent.visibility = View.VISIBLE
                    // 도수 필터가 선택된 상태라면 해당하는 라디오버튼 체크
                    if (activityViewModel.alcoholButtonSelected) {
                        activityViewModel.selectedAlcoholFilter.value?.let { value ->
                            val radioButtonId = alcoholFilterMap.entries.find { it.value == value }?.key
                            radioButtonId?.let { radioGroupFilterAlcoholContent.check(it) }
                        }
                    }
                }
                3 -> {
                    constraintLayoutFilterBottomSheetBase.visibility = View.VISIBLE
                    // 베이스 필터가 선택된 상태라면 해당하는 라디오버튼 체크
                    if (activityViewModel.baseButtonSelected) {
                        activityViewModel.selectedBaseFilter.value?.let { baseSpirit ->
                            radioButtons[baseSpirit]?.isChecked = true
                        }
                    }
                }
            }
        }
    }

    private val zzimFilterMap = mapOf(
        R.id.radioButtonFilterZzimHigh to 1,
        R.id.radioButtonFilterZzimLow to 0
    )

    private val timeFilterMap = mapOf(
        R.id.radioButtonFilterTimeHigh to 1,
        R.id.radioButtonFilterTimeLow to 0
    )

    private val alcoholFilterMap = mapOf(
        R.id.radioButtonFilterAlcoholContentHigh to 1,
        R.id.radioButtonFilterAlcoholContentLow to 0
    )

    private val radioButtons by lazy {
        mapOf(
            "진" to binding.radioButtonFilterBaseJin,
            "럼" to binding.radioButtonFilterBaseRum,
            "보드카" to binding.radioButtonFilterBaseVodka,
            "위스키" to binding.radioButtonFilterBaseWhiskey,
            "데킬라" to binding.radioButtonFilterBaseTequila,
            "리큐어" to binding.radioButtonFilterBaseLiqueur,
            "와인" to binding.radioButtonFilterBaseWine,
            "브랜디" to binding.radioButtonFilterBaseBrandy,
            "기타" to binding.radioButtonFilterBaseEtc
        )
    }

    private fun clearOtherSelections(selectedGroup: RadioGroup?) {
        // 라디오 그룹 초기화
        radioGroups.forEach { group ->
            if (group != selectedGroup) {
                group.clearCheck()
            }
        }

        // 베이스 선택 처리
        if (selectedGroup == null) {
            // 현재 선택된 베이스 값 기준으로 처리
            gridRadioButtons.forEach { button ->
                val buttonBase = radioButtons.entries.find { it.value == button }?.key
                button.isChecked = buttonBase == tempSelectedBase
            }
        } else {
            // 다른 필터 그룹이 선택된 경우 모든 베이스 버튼 해제
            gridRadioButtons.forEach { it.isChecked = false }
            tempSelectedBase = null
        }
    }

    private fun initRadioButtons() {
        // 라디오 그룹의 선택 리스너 설정
        radioGroups.forEach { group ->
            group.setOnCheckedChangeListener { _, _ ->
                clearOtherSelections(group)
            }
        }

        // 베이스주 버튼 클릭 리스너
        gridRadioButtons.forEach { radioButton ->
            radioButton.setOnClickListener {
                tempSelectedBase = radioButtons.entries.find { it.value == radioButton }?.key
                clearOtherSelections(null)
                radioButton.isChecked = true
            }
        }

        // 개별 라디오 그룹 체크 리스너
        binding.apply {
            radioGroupFilterZzim.setOnCheckedChangeListener { _, checkedId ->
                if (checkedId != -1) {
                    activityViewModel.updateZzimButtonState(true)
                    clearOtherSelections(radioGroupFilterZzim)
                }
            }

            radioGroupFilterTime.setOnCheckedChangeListener { _, checkedId ->
                if (checkedId != -1) {
                    activityViewModel.updateTimeButtonState(true)
                    clearOtherSelections(radioGroupFilterTime)
                }
            }

            radioGroupFilterAlcoholContent.setOnCheckedChangeListener { _, checkedId ->
                if (checkedId != -1) {
                    activityViewModel.updateAlcoholButtonState(true)
                    clearOtherSelections(radioGroupFilterAlcoholContent)
                }
            }

            // 찾기 버튼 클릭 시 필터 적용
            textViewFilterSearch.setOnClickListener {
                applySelectedFilters()
                dismiss()
            }
        }
    }

    private fun applySelectedFilters() {
        binding.apply {
            zzimFilterMap[radioGroupFilterZzim.checkedRadioButtonId]?.let {
                activityViewModel.setZzimFilter(it)
                activityViewModel.updateZzimButtonState(true)
            }

            timeFilterMap[radioGroupFilterTime.checkedRadioButtonId]?.let {
                activityViewModel.setTimeFilter(it)
                activityViewModel.updateTimeButtonState(true)
            }

            alcoholFilterMap[radioGroupFilterAlcoholContent.checkedRadioButtonId]?.let {
                activityViewModel.setAlcoholFilter(it)
                activityViewModel.updateAlcoholButtonState(true)
            }

            tempSelectedBase?.let {
                activityViewModel.setBaseFilter(it)
                activityViewModel.updateBaseButtonState(true)
            }
        }
    }

    companion object {
        fun newInstance(filter: Int): FilterBottomSheetFragment {
            return FilterBottomSheetFragment().apply {
                arguments = Bundle().apply { putInt("filter", filter) }
            }
        }
    }
}