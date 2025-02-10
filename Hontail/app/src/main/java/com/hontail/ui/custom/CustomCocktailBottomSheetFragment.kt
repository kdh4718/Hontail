package com.hontail.ui.custom

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.hontail.R
import com.hontail.base.BaseBottomSheetFragment
import com.hontail.databinding.FragmentCustomCocktailBottomSheetBinding
import com.hontail.ui.MainActivity

class CustomCocktailBottomSheetFragment: BaseBottomSheetFragment<FragmentCustomCocktailBottomSheetBinding>(
    FragmentCustomCocktailBottomSheetBinding::bind,
    R.layout.fragment_custom_cocktail_bottom_sheet
) {

    private lateinit var mainActivity: MainActivity
    private lateinit var customCocktailBottomSheetAdapter: CustomCocktailBottomSheetAdapter

    // currentUnit 매개변수 추가
    companion object {
        fun newInstance(currentUnit: String): CustomCocktailBottomSheetFragment {
            return CustomCocktailBottomSheetFragment().apply {
                this.currentSelectedUnit = currentUnit
            }
        }
    }

    private var currentSelectedUnit: String = "ml"  // 기본값

    private val unitList: MutableList<UnitItem> by lazy {
        mutableListOf(
            UnitItem("ml", currentSelectedUnit == "ml"),
            UnitItem("shot", currentSelectedUnit == "shot"),
            UnitItem("dash", currentSelectedUnit == "dash"),
            UnitItem("oz", currentSelectedUnit == "oz"),
            UnitItem("slice", currentSelectedUnit == "slice"),
            UnitItem("leaves", currentSelectedUnit == "leaves"),
        )
    }

    interface UnitSelectListener {
        fun onUnitSelected(unit: String)
    }

    var unitSelectListener: UnitSelectListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
    }

    private fun initAdapter() {
        binding.apply {
            customCocktailBottomSheetAdapter = CustomCocktailBottomSheetAdapter(unitList) { selectedUnit ->
                unitSelectListener?.onUnitSelected(selectedUnit)
                dismiss()
            }
            recyclerViewCustomCocktailBottomSheet.layoutManager = LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false)
            recyclerViewCustomCocktailBottomSheet.adapter = customCocktailBottomSheetAdapter
        }
    }
}

data class UnitItem(val unitName: String, var unitSelected: Boolean)