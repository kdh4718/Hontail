package com.hontail.ui.cocktail

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hontail.R
import com.hontail.base.BaseBottomSheetFragment
import com.hontail.databinding.FragmentCocktailCookBottomSheetBinding
import com.hontail.ui.MainActivity
import com.hontail.util.CommonUtils

class CocktailCookBottomSheetFragment : BaseBottomSheetFragment<FragmentCocktailCookBottomSheetBinding>(
    FragmentCocktailCookBottomSheetBinding::bind,
R.layout.fragment_cocktail_cook_bottom_sheet
) {

    private lateinit var mainActivity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initEvent()
    }

    private fun initEvent() {

        binding.apply {

            // 바텀시트 닫기.
            imageViewCookClose.setOnClickListener {
                dismiss()
            }

            // cookmode 들어가기.
            imageViewCookTurnScreen.setOnClickListener {
                mainActivity.changeFragment(CommonUtils.MainFragmentName.COCKTAIL_RECIPE_FRAGMENT)
                dismiss()
            }
        }
    }
}