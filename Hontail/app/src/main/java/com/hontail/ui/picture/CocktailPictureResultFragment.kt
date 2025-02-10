package com.hontail.ui.picture

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.hontail.R
import com.hontail.base.BaseFragment
import com.hontail.databinding.FragmentCocktailPictureResultBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.MainActivityViewModel
import com.hontail.ui.picture.viewmodel.CocktailPictureResultFragmentViewModel

class CocktailPictureResultFragment : BaseFragment<FragmentCocktailPictureResultBinding>(
    FragmentCocktailPictureResultBinding::bind,
    R.layout.fragment_cocktail_picture_result
) {
    private lateinit var mainActivity: MainActivity
    private val activityViewModel: MainActivityViewModel by activityViewModels()
    private val viewModel: CocktailPictureResultFragmentViewModel by viewModels()
    private val bottomSheet = FilterBottomSheetFragment()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.ingredientList = activityViewModel.ingredientList.value!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initRecyclerView()
        setupBackButton()
    }

    private fun setupBackButton() {
        binding.imageViewPictureResultGoBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    fun initData(){
        viewModel.getIngredientAnalyze()
    }

    private fun initRecyclerView() {
        val dataList = listOf(
            "Salt", "Mint", "Sugar", "Lime", "Ice",
            "Rum", "Soda", "Basil", "Peach", "Cherry",
            "Lemon", "Orange"
        )

        val pictureTopData = PictureResultType.Top(
            suggestion = "hyunn님, 오늘은 이 재료로\n딱 맞는 칵테일을 만들어 볼까요?",
            ingredients = dataList
        )

        val pictureBottomData = PictureResultType.Bottom(
            cocktailCount = "칵테일 24개",
            filters = listOf(),  // 필터 데이터
            cocktails = listOf() // 칵테일 데이터
        )

        binding.recyclerViewPictureResult.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = ConcatAdapter(
                PictureTopAdapter(requireContext(), pictureTopData),
                PictureBottomAdapter(requireContext(), pictureBottomData)
            )
        }
    }

    sealed class PictureResultType {
        data class Top(
            val suggestion: String,
            val ingredients: List<String>
        ) : PictureResultType()

        data class Bottom(
            val cocktailCount: String,
            val filters: List<String>,
            val cocktails: List<String>
        ) : PictureResultType()
    }
}
