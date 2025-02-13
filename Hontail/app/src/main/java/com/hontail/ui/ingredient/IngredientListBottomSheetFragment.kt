package com.hontail.ui.ingredient

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.hontail.R
import com.hontail.base.BaseBottomSheetFragment
import com.hontail.databinding.FragmentIngredientListBinding
import com.hontail.ui.MainActivityViewModel

class IngredientListBottomSheetFragment : BaseBottomSheetFragment<FragmentIngredientListBinding>(
    FragmentIngredientListBinding::bind,
    R.layout.fragment_ingredient_list
) {
    private val viewModel: MainActivityViewModel by activityViewModels()
    private lateinit var ingredientAdapter: IngredientAdapter

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val bottomSheet = (it as BottomSheetDialog).findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let { sheet ->
                sheet.setBackgroundResource(android.R.color.transparent)
                val parent = sheet.parent as ViewGroup
                parent.setBackgroundResource(android.R.color.transparent)

                val behavior = BottomSheetBehavior.from(sheet)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true

                val layoutParams = sheet.layoutParams
                layoutParams.height = (resources.displayMetrics.heightPixels * 0.9).toInt()
                sheet.layoutParams = layoutParams
            }
        }
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadDummyData()
    }

    private fun setupRecyclerView() {
        ingredientAdapter = IngredientAdapter()
        binding.recyclerViewIngredient.apply {
            adapter = ingredientAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        ingredientAdapter.itemClickListener = { position ->
            val selectedIngredient = ingredientAdapter.ingredients[position]
            dismiss()
        }
    }

    private fun loadDummyData() {
        val dummyIngredients = listOf(
            Ingredient("감미료 및 시럽", null),
            Ingredient("신선한 과일 및 장식", null),
            Ingredient("주스 및 퓌레", null),
            Ingredient("베이스 주류", null),
            Ingredient("맥주 및 사이다", null),
            Ingredient("향신료 및 조미료", null),
            Ingredient("비터스", null),
            Ingredient("차 및 인퓨전", null),
            Ingredient("무알코올 음료", null),
            Ingredient("기타", null),
        )
        ingredientAdapter.submitList(dummyIngredients)
    }

    companion object {
        fun newInstance(): IngredientListBottomSheetFragment {
            return IngredientListBottomSheetFragment()
        }
    }
}