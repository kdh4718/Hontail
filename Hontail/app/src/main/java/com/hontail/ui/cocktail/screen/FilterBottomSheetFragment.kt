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
    private var tempSelectedBase: String? = null  // 임시 선택값 저장용 변수 추가

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initRadioButtons()
        setupObservers()
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

    private fun setupObservers() {
        // Zzim Filter Observer
        activityViewModel.selectedZzimFilter.observe(viewLifecycleOwner) { radioButtonId ->
            radioButtonId?.let { binding.radioGroupFilterZzim.check(it) }
        }

        // Time Filter Observer
        activityViewModel.selectedTimeFilter.observe(viewLifecycleOwner) { radioButtonId ->
            radioButtonId?.let { binding.radioGroupFilterTime.check(it) }
        }

        // Alcohol Filter Observer
        activityViewModel.selectedAlcoholFilter.observe(viewLifecycleOwner) { radioButtonId ->
            radioButtonId?.let { binding.radioGroupFilterAlcoholContent.check(it) }
        }

        // Base Spirit Filter Observer
        activityViewModel.selectedBaseFilter.observe(viewLifecycleOwner) { baseSpirit ->
            Log.d(TAG, "Base Filter Observer - Selected: $baseSpirit")
            radioButtons.values.forEach { it.isChecked = false }
            radioButtons[baseSpirit]?.isChecked = true
        }
    }

    private fun initView() {
        val filterPosition = arguments?.getInt("filter", 0) ?: 0

        binding.apply {
            listOf(
                constraintLayoutFilterBottomSheetZzim,
                constraintLayoutFilterBottomSheetTime,
                constraintLayoutFilterBottomSheetAlcoholContent,
                constraintLayoutFilterBottomSheetBase
            ).forEach { it.visibility = View.GONE }

            when (filterPosition) {
                0 -> constraintLayoutFilterBottomSheetZzim.visibility = View.VISIBLE
                1 -> constraintLayoutFilterBottomSheetTime.visibility = View.VISIBLE
                2 -> constraintLayoutFilterBottomSheetAlcoholContent.visibility = View.VISIBLE
                3 -> constraintLayoutFilterBottomSheetBase.visibility = View.VISIBLE
            }
        }

        // Initialize the selected filters based on ViewModel's stored values
        activityViewModel.selectedZzimFilter.value?.let { binding.radioGroupFilterZzim.check(it) }
        activityViewModel.selectedTimeFilter.value?.let { binding.radioGroupFilterTime.check(it) }
        activityViewModel.selectedAlcoholFilter.value?.let { binding.radioGroupFilterAlcoholContent.check(it) }
        activityViewModel.selectedBaseFilter.value?.let {
            radioButtons[it]?.isChecked = true
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

        // 베이스 선택 처리 개선
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

        // 베이스주 버튼 클릭 리스너 수정
        gridRadioButtons.forEach { radioButton ->
            radioButton.setOnClickListener {
                // 즉시 필터 적용하지 않고 임시 저장만 함
                tempSelectedBase = radioButtons.entries.find { it.value == radioButton }?.key
                clearOtherSelections(null)

                // 시각적 상태 변경
                radioButton.isChecked = true
            }
        }

        // 개별 라디오 그룹에 대한 체크 리스너 설정
        binding.apply {
            radioGroupFilterZzim.setOnCheckedChangeListener { _, checkedId ->
                Log.d(TAG, "initRadioButtons: $checkedId")
                if (checkedId != -1) {
                    activityViewModel.updateZzimButtonState(true)
                    clearOtherSelections(radioGroupFilterZzim)
                }
            }

            radioGroupFilterTime.setOnCheckedChangeListener { _, checkedId ->
                Log.d(TAG, "initRadioButtons: $checkedId")
                if (checkedId != -1) {
                    activityViewModel.updateTimeButtonState(true)
                    clearOtherSelections(radioGroupFilterTime)
                }
            }

            radioGroupFilterAlcoholContent.setOnCheckedChangeListener { _, checkedId ->
                Log.d(TAG, "initRadioButtons: $checkedId")
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
                Log.d(TAG, "Filter applySelectedFilters - Zzim: $it")
                activityViewModel.setZzimFilter(it)
                activityViewModel.updateZzimButtonState(true)
            }

            timeFilterMap[radioGroupFilterTime.checkedRadioButtonId]?.let {
                Log.d(TAG, "Filter applySelectedFilters - Time: $it")
                activityViewModel.setTimeFilter(it)
                activityViewModel.updateTimeButtonState(true)
            }

            alcoholFilterMap[radioGroupFilterAlcoholContent.checkedRadioButtonId]?.let {
                Log.d(TAG, "Filter applySelectedFilters - Alcohol: $it")
                activityViewModel.setAlcoholFilter(it)
                activityViewModel.updateAlcoholButtonState(true)
            }

            // 베이스주 필터는 임시 저장된 값을 사용하여 적용
            tempSelectedBase?.let {
                Log.d(TAG, "Filter applySelectedFilters - Base: $it")
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