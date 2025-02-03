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

    private val unitList = mutableListOf(
        UnitItem("ml", true),
        UnitItem("shot", false),
        UnitItem("dash",false),
        UnitItem("oz", false),
        UnitItem("slice", false),
        UnitItem("leaves", false),
    )

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
    }

    // 리사이클러뷰 연결
    private fun initAdapter() {

        binding.apply {

            customCocktailBottomSheetAdapter = CustomCocktailBottomSheetAdapter(unitList)

            recyclerViewCustomCocktailBottomSheet.layoutManager = LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false)
            recyclerViewCustomCocktailBottomSheet.adapter = customCocktailBottomSheetAdapter
        }
    }

}

data class UnitItem(val unitName: String, var unitSelected: Boolean)