package com.hontail.ui.ingredient.screen

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import com.hontail.R
import com.hontail.base.BaseFragment
import com.hontail.databinding.FragmentIngredientAddBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.MainActivityViewModel

class IngredientAddFragment : BaseFragment<FragmentIngredientAddBinding>(
    FragmentIngredientAddBinding::bind,
    R.layout.fragment_ingredient_add
) {
    private lateinit var mainActivity: MainActivity
    private val activityViewModel: MainActivityViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainActivity.hideBottomNav(true)

        initToolbar()
        initEvent()
    }

    private fun initToolbar() {

        binding.apply {

            toolbarIngredientAdd.apply {

                setNavigationIcon(R.drawable.go_back)
                setNavigationOnClickListener {
                    parentFragmentManager.popBackStack("IngredientAddFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                }
            }
        }
    }

    private fun initEvent() {

        binding.apply {

            // 분류 바텀 시트
            buttonIngredientAddCategory.setOnClickListener {

                val currentSelectedUnit = textViewIngredientAddCategory.text.toString()

                val bottomSheetFragment = IngredientBottomSheetFragment.newInstance(currentSelectedUnit)

                bottomSheetFragment.onUnitSelectedListener = object : OnUnitSelectedListener {
                    override fun onUnitSelected(unitItem: UnitItem) {
                        textViewIngredientAddCategory.text = unitItem.unitName
                    }
                }

                bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
            }

            // 재료 요청 보내기.
            buttonIngredientAddRequest.setOnClickListener {

                if(textViewIngredientAddName.text.isNullOrEmpty() || textViewIngredientAddCategory.text.isNullOrEmpty() || textViewIngredientAddAlcoholContent.text.isNullOrEmpty()) {
                    Toast.makeText(mainActivity, "빈 곳을 다 채워주세요.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                Toast.makeText(mainActivity, "재료 요청이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack("IngredientAddFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
        }
    }
}