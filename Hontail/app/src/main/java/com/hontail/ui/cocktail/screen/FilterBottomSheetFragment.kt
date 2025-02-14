package com.hontail.ui.cocktail.screen

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.hontail.R
import com.hontail.base.BaseBottomSheetFragment
import com.hontail.databinding.FragmentFilterBottomSheetBinding
import com.hontail.ui.MainActivityViewModel
import com.hontail.ui.cocktail.viewmodel.CocktailListFragmentViewModel

class FilterBottomSheetFragment : BaseBottomSheetFragment<FragmentFilterBottomSheetBinding>(
    FragmentFilterBottomSheetBinding::bind,
    R.layout.fragment_filter_bottom_sheet
){
    private val activityViewModel: MainActivityViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initRadioButtons()
        setupObservers()
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

        activityViewModel.selectedBaseFilter.observe(viewLifecycleOwner) { baseSpirit ->
            baseSpirit.takeIf { it.isNotEmpty() }?.let {
                radioButtons[it]?.isChecked = true
            }
        }
    }

    private fun initView() {
        val filterPosition = arguments?.getInt("filter", 0) ?: 0

        binding.apply {
            constraintLayoutFilterBottomSheetZzim.visibility = View.GONE
            constraintLayoutFilterBottomSheetTime.visibility = View.GONE
            constraintLayoutFilterBottomSheetAlcoholContent.visibility = View.GONE
            constraintLayoutFilterBottomSheetBase.visibility = View.GONE

            when (filterPosition) {
                0 -> constraintLayoutFilterBottomSheetZzim.visibility = View.VISIBLE
                1 -> constraintLayoutFilterBottomSheetTime.visibility = View.VISIBLE
                2 -> constraintLayoutFilterBottomSheetAlcoholContent.visibility = View.VISIBLE
                3 -> constraintLayoutFilterBottomSheetBase.visibility = View.VISIBLE
            }
        }
    }

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

    private fun initRadioButtons() {
        binding.apply {
            radioGroupFilterZzim.setOnCheckedChangeListener { _, checkedId ->
                if (checkedId != -1) activityViewModel.setZzimFilter(checkedId)
            }

            radioGroupFilterTime.setOnCheckedChangeListener { _, checkedId ->
                if (checkedId != -1) activityViewModel.setTimeFilter(checkedId)
            }

            radioGroupFilterAlcoholContent.setOnCheckedChangeListener { _, checkedId ->
                if (checkedId != -1) activityViewModel.setAlcoholFilter(checkedId)
            }
        }

        radioButtons.forEach { (key, radioButton) ->
            radioButton.setOnClickListener {
                activityViewModel.setBaseFilter(key)
                radioButtons.values.forEach { it.isChecked = false }
                radioButton.isChecked = true
            }
        }

        binding.textViewFilterSearch.setOnClickListener {
            applySelectedFilters()
            dismiss()
        }
    }

    private fun applySelectedFilters() {
        binding.apply {
            radioGroupFilterZzim.checkedRadioButtonId.let {
                if (it != -1) activityViewModel.setZzimFilter(it)
            }
            radioGroupFilterTime.checkedRadioButtonId.let {
                if (it != -1) activityViewModel.setTimeFilter(it)
            }
            radioGroupFilterAlcoholContent.checkedRadioButtonId.let {
                if (it != -1) activityViewModel.setAlcoholFilter(it)
            }
            radioButtons.entries.find { it.value.isChecked }?.key?.let {
                activityViewModel.setBaseFilter(it)
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
