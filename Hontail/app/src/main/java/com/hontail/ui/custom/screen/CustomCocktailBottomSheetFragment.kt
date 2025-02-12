package com.hontail.ui.custom.screen

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.hontail.R
import com.hontail.base.BaseBottomSheetFragment
import com.hontail.databinding.FragmentCustomCocktailBottomSheetBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.custom.adapter.CustomCocktailBottomSheetAdapter

class CustomCocktailBottomSheetFragment: BaseBottomSheetFragment<FragmentCustomCocktailBottomSheetBinding>(
    FragmentCustomCocktailBottomSheetBinding::bind,
    R.layout.fragment_custom_cocktail_bottom_sheet
) {

    private lateinit var mainActivity: MainActivity
    private lateinit var customCocktailBottomSheetAdapter: CustomCocktailBottomSheetAdapter

    private var initialSelectedUnit: String = "ml"

    private val unitList = mutableListOf(
        UnitItem("ml", true),
        UnitItem("shot", false),
        UnitItem("dash",false),
        UnitItem("oz", false),
        UnitItem("slice", false),
        UnitItem("leaves", false),
    )

    companion object {
        private const val ARG_SELECTED_UNIT = "selected_unit"

        // 현재 선택된 단위를 인자로 받아 BottomSheetFragment 인스턴스를 생성하는 함수
        fun newInstance(selectedUnit: String): CustomCocktailBottomSheetFragment {
            val fragment = CustomCocktailBottomSheetFragment()
            val args = Bundle()
            args.putString(ARG_SELECTED_UNIT, selectedUnit)
            fragment.arguments = args
            return fragment
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.getString(ARG_SELECTED_UNIT)?.let {
            initialSelectedUnit = it
        }
        // unitList에서 initialSelectedUnit과 일치하는 항목만 true로 설정
        unitList.forEach { it.unitSelected = (it.unitName == initialSelectedUnit) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        initEvent()
    }

    var onUnitSelectedListener: OnUnitSelectedListener? = null

    // 리사이클러뷰 연결
    private fun initAdapter() {
        binding.apply {

            customCocktailBottomSheetAdapter = CustomCocktailBottomSheetAdapter(unitList)

            recyclerViewCustomCocktailBottomSheet.layoutManager = LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false)
            recyclerViewCustomCocktailBottomSheet.adapter = customCocktailBottomSheetAdapter
        }
    }

    // 이벤트
    private fun initEvent() {

        binding.apply {

            customCocktailBottomSheetAdapter.customCocktailBottomSheetListener = object : CustomCocktailBottomSheetAdapter.ItemOnClickListener {
                override fun onClick(position: Int) {

                    unitList.forEach {
                        it.unitSelected = false
                    }

                    unitList[position].unitSelected = true

                    customCocktailBottomSheetAdapter.notifyDataSetChanged()

                    onUnitSelectedListener?.onUnitSelected(unitList[position])

                    dismiss()
                }
            }
        }
    }

}

data class UnitItem(val unitName: String, var unitSelected: Boolean)

interface OnUnitSelectedListener {
    fun onUnitSelected(unitItem: UnitItem)
}