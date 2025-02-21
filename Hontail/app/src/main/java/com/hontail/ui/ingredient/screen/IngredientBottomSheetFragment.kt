package com.hontail.ui.ingredient.screen

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.hontail.R
import com.hontail.base.BaseBottomSheetFragment
import com.hontail.databinding.FragmentIngredientListBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.ingredient.adapter.IngredientAdapter

class IngredientBottomSheetFragment: BaseBottomSheetFragment<FragmentIngredientListBinding>(
    FragmentIngredientListBinding::bind,
    R.layout.fragment_ingredient_list
) {

    private lateinit var mainActivity: MainActivity
    private lateinit var ingredientAdapter: IngredientAdapter

    private var initialSelectedUnit: String = "감미료 및 시럽"
    var onUnitSelectedListener: OnUnitSelectedListener? = null

    private val unitList = mutableListOf(
        UnitItem("감미료 및 시럽", false),
        UnitItem("신선한 과일 및 장식", false),
        UnitItem("주스 및 퓌레", false),
        UnitItem("베이스 주류", false),
        UnitItem("맥주 및 사이다", false),
        UnitItem("향신료 및 조미료", false),
        UnitItem("비터스", false),
        UnitItem("차 및 인퓨전", false),
        UnitItem("무알코올 음료", false),
        UnitItem("기타", false),
    )

    companion object {
        private const val ARG_SELECTED_UNIT = "selected_unit"

        fun newInstance(selectedUnit: String): IngredientBottomSheetFragment {
            val fragment = IngredientBottomSheetFragment()
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
        unitList.forEach {
            it.unitSelected = (it.unitName == initialSelectedUnit)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        initEvent()
    }

    private fun initAdapter() {

        binding.apply {

            ingredientAdapter = IngredientAdapter(unitList)

            recyclerViewIngredientAddBottomSheet.layoutManager = LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false)
            recyclerViewIngredientAddBottomSheet.adapter = ingredientAdapter
        }
    }

    private fun initEvent() {

        binding.apply {

            ingredientAdapter.ingredientListener = object : IngredientAdapter.ItemOnClickListener {
                override fun onClick(position: Int) {

                    unitList.forEach {
                        it.unitSelected = false
                    }

                    unitList[position].unitSelected = true

                    ingredientAdapter.notifyDataSetChanged()

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