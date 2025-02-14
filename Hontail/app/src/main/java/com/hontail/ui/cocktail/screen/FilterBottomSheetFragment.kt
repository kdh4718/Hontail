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
        activityViewModel.selectedZzimFilter.observe(viewLifecycleOwner) { radioButtonId ->
            radioButtonId?.let { binding.radioGroupFilterZzim.check(it) }
        }

        activityViewModel.selectedTimeFilter.observe(viewLifecycleOwner) { radioButtonId ->
            radioButtonId?.let { binding.radioGroupFilterTime.check(it) }
        }

        activityViewModel.selectedAlcoholFilter.observe(viewLifecycleOwner) { radioButtonId ->
            radioButtonId?.let { binding.radioGroupFilterAlcoholContent.check(it) }
        }

        // 🔥 베이스 주류 필터 UI 반영 수정
        activityViewModel.selectedBaseFilter.observe(viewLifecycleOwner) { baseSpirit ->
            Log.d(TAG, "setupObservers - Base Selected: $baseSpirit")
            radioButtons.values.forEach { it.isChecked = false } // 전체 해제 후
            radioButtons[baseSpirit]?.isChecked = true // 선택한 것만 체크
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
                clearOtherSelections(null)
                activityViewModel.setBaseFilter(radioButtons.entries.find { it.value == radioButton }?.key ?: "")
            }
        }

        binding.apply {
            radioGroupFilterZzim.setOnCheckedChangeListener { _, checkedId ->
                Log.d(TAG, "initRadioButtons: $checkedId")
                if (checkedId != -1) {
                    activityViewModel.setZzimFilter(checkedId)
                    activityViewModel.updateZzimButtonState(true)
                }
            }

            radioGroupFilterTime.setOnCheckedChangeListener { _, checkedId ->
                Log.d(TAG, "initRadioButtons: $checkedId")
                if (checkedId != -1) {
                    activityViewModel.setTimeFilter(checkedId)
                    activityViewModel.updateTimeButtonState(true)
                }
            }

            radioGroupFilterAlcoholContent.setOnCheckedChangeListener { _, checkedId ->
                Log.d(TAG, "initRadioButtons: $checkedId")
                if (checkedId != -1) {
                    activityViewModel.setAlcoholFilter(checkedId)
                    activityViewModel.updateAlcoholButtonState(true)
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