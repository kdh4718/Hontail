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
        restoreFilterStates()
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

    private fun restoreFilterStates() {
        val filterPosition = arguments?.getInt("filter", 0) ?: 0

        when (filterPosition) {
            0 -> {
                // 찜 필터 상태 복원
                if (activityViewModel.zzimButtonSelected) {
                    activityViewModel.selectedZzimFilter.value?.let { value ->
                        val radioId = when (value) {
                            1 -> R.id.radioButtonFilterZzimHigh
                            0 -> R.id.radioButtonFilterZzimLow
                            else -> null
                        }
                        radioId?.let { binding.radioGroupFilterZzim.check(it) }
                    }
                }
            }
            1 -> {
                // 시간 필터 상태 복원
                if (activityViewModel.timeButtonSelected) {
                    activityViewModel.selectedTimeFilter.value?.let { value ->
                        val radioId = when (value) {
                            1 -> R.id.radioButtonFilterTimeHigh
                            0 -> R.id.radioButtonFilterTimeLow
                            else -> null
                        }
                        radioId?.let { binding.radioGroupFilterTime.check(it) }
                    }
                }
            }
            2 -> {
                // 도수 필터 상태 복원
                if (activityViewModel.alcoholButtonSelected) {
                    activityViewModel.selectedAlcoholFilter.value?.let { value ->
                        val radioId = when (value) {
                            1 -> R.id.radioButtonFilterAlcoholContentHigh
                            0 -> R.id.radioButtonFilterAlcoholContentLow
                            else -> null
                        }
                        radioId?.let { binding.radioGroupFilterAlcoholContent.check(it) }
                    }
                }
            }
            3 -> {
                // 베이스 필터 상태 복원
                if (activityViewModel.baseButtonSelected) {
                    activityViewModel.selectedBaseFilter.value?.let { baseSpirit ->
                        radioButtons[baseSpirit]?.isChecked = true
                    }
                }
            }
        }
    }

    private fun setupObservers() {
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
        radioGroups.forEach { group ->
            if (group != selectedGroup) {
                group.clearCheck()
            }
        }

        if (selectedGroup == null) {
            gridRadioButtons.forEach { it.isChecked = false }
        }
    }

    private fun initRadioButtons() {
        radioGroups.forEach { group ->
            group.setOnCheckedChangeListener { _, _ ->
                clearOtherSelections(group)
            }
        }

        gridRadioButtons.forEach { radioButton ->
            radioButton.setOnClickListener {
                val selectedBase = radioButtons.entries.find { it.value == radioButton }?.key
                selectedBase?.let {
                    Log.d(TAG, "initRadioButtons - Base Selected: $it")
                    activityViewModel.setBaseFilter(it)
                    clearOtherSelections(null)
                }
            }
        }

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

            textViewFilterSearch.setOnClickListener {
                applySelectedFilters()
                dismiss()
            }
        }
    }

    private fun applySelectedFilters() {
        binding.apply {
            val zzimId = radioGroupFilterZzim.checkedRadioButtonId
            if (zzimId != -1) {
                zzimFilterMap[zzimId]?.let {
                    Log.d(TAG, "Filter applySelectedFilters - Zzim: $it")
                    activityViewModel.setZzimFilter(it)
                    activityViewModel.updateZzimButtonState(true)
                }
            }

            val timeId = radioGroupFilterTime.checkedRadioButtonId
            if (timeId != -1) {
                timeFilterMap[timeId]?.let {
                    Log.d(TAG, "Filter applySelectedFilters - Time: $it")
                    activityViewModel.setTimeFilter(it)
                    activityViewModel.updateTimeButtonState(true)
                }
            }

            val alcoholId = radioGroupFilterAlcoholContent.checkedRadioButtonId
            if (alcoholId != -1) {
                alcoholFilterMap[alcoholId]?.let {
                    Log.d(TAG, "Filter applySelectedFilters - Alcohol: $it")
                    activityViewModel.setAlcoholFilter(it)
                    activityViewModel.updateAlcoholButtonState(true)
                }
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