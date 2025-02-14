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
        setupBackButton()
        setupCategoryButton()
        initEvent()
    }

    private fun setupBackButton() {
        binding.imageViewIngredientArrow.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun setupCategoryButton() {
        binding.textViewIngredientSort.setOnClickListener {
            val bottomSheetFragment = IngredientListBottomSheetFragment.newInstance()
            bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
        }
    }

    private fun initEvent() {

        binding.apply {

            textViewIngredientRequest.setOnClickListener {
                Toast.makeText(mainActivity, "재료 요청이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack("IngredientAddFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
        }
    }
}