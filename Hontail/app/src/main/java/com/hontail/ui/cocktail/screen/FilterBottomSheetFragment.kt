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
            // Reset all radio buttons to unselected before selecting the appropriate one
            radioButtons.values.forEach { it.isChecked = false }
            radioButtons[baseSpirit]?.isChecked = true // Base spirit selected
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
            // Ensure the selected base spirit is checked when the view is created
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
        // 다른 라디오 그룹에서 선택된 라디오 버튼을 취소
        radioGroups.forEach { group ->
            if (group != selectedGroup) {
                group.clearCheck()
            }
        }

        // gridRadioButtons에서 선택된 라디오 버튼이 있을 경우 다른 라디오 그룹들을 취소
        if (selectedGroup == null) {
            gridRadioButtons.forEach { it.isChecked = false }
        }
    }

    private fun initRadioButtons() {
        // 라디오 그룹의 선택 리스너 설정
        radioGroups.forEach { group ->
            group.setOnCheckedChangeListener { _, _ ->
                clearOtherSelections(group) // 선택된 그룹 외 다른 그룹 취소
            }
        }

        // gridRadioButtons에 대한 클릭 리스너 설정
        gridRadioButtons.forEach { radioButton ->
            radioButton.setOnClickListener {
                // 클릭된 라디오 버튼에 해당하는 값을 viewModel에 전달
                val selectedBase = radioButtons.entries.find { it.value == radioButton }?.key
                selectedBase?.let {
                    Log.d(TAG, "initRadioButtons - Base Selected: $it")
                    activityViewModel.setBaseFilter(it)
                    clearOtherSelections(null) // 다른 라디오 그룹 취소
                }
            }
        }

        // 개별 라디오 그룹에 대한 체크 리스너 설정
        binding.apply {
            radioGroupFilterZzim.setOnCheckedChangeListener { _, checkedId ->
                Log.d(TAG, "initRadioButtons: $checkedId")
                if (checkedId != -1) {
                    activityViewModel.setZzimFilter(checkedId)
                    activityViewModel.updateZzimButtonState(true)
                    clearOtherSelections(radioGroupFilterZzim) // 해당 그룹 외 다른 그룹 취소
                }
            }

            radioGroupFilterTime.setOnCheckedChangeListener { _, checkedId ->
                Log.d(TAG, "initRadioButtons: $checkedId")
                if (checkedId != -1) {
                    activityViewModel.setTimeFilter(checkedId)
                    activityViewModel.updateTimeButtonState(true)
                    clearOtherSelections(radioGroupFilterTime) // 해당 그룹 외 다른 그룹 취소
                }
            }

            radioGroupFilterAlcoholContent.setOnCheckedChangeListener { _, checkedId ->
                Log.d(TAG, "initRadioButtons: $checkedId")
                if (checkedId != -1) {
                    activityViewModel.setAlcoholFilter(checkedId)
                    activityViewModel.updateAlcoholButtonState(true)
                    clearOtherSelections(radioGroupFilterAlcoholContent) // 해당 그룹 외 다른 그룹 취소
                }
            }

            // 베이스 주류 필터 적용
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

            radioButtons.entries.find { it.value.isChecked }?.key?.let {
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