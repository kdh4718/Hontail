package com.hontail.ui.picture

import android.content.Context
import androidx.fragment.app.activityViewModels
import com.hontail.R
import com.hontail.base.BaseFragment
import com.hontail.databinding.FragmentCocktailTakePictureBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.MainActivityViewModel

class CocktailTakePictureFragment : BaseFragment<FragmentCocktailTakePictureBinding>(
    FragmentCocktailTakePictureBinding::bind,
    R.layout.fragment_cocktail_take_picture
) {
    private lateinit var mainActivity: MainActivity
    private val activityViewModel: MainActivityViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }
}