package com.hontail.ui.picture.screen

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.hontail.R
import com.hontail.base.BaseFragment
import com.hontail.data.model.response.CocktailListResponse
import com.hontail.databinding.FragmentCocktailPictureResultBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.MainActivityViewModel
import com.hontail.ui.picture.adapter.PictureBottomAdapter
import com.hontail.ui.picture.adapter.PictureTopAdapter
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
        viewModel.detectedTextList = activityViewModel.ingredientList.value!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        initData()
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
        binding.recyclerViewPictureResult.layoutManager = LinearLayoutManager(requireContext())

        // 어댑터를 미리 생성 (초기 빈 데이터)
        val pictureTopAdapter = PictureTopAdapter(requireContext(), PictureResultType.Top("", emptyList()))
        val pictureBottomAdapter = PictureBottomAdapter(requireContext(), PictureResultType.Bottom("", emptyList(), emptyList()))

        // 어댑터를 ConcatAdapter로 묶기
        val concatAdapter = ConcatAdapter(pictureTopAdapter, pictureBottomAdapter)
        binding.recyclerViewPictureResult.adapter = concatAdapter

        // ViewModel의 데이터 변경 감지 및 UI 업데이트
        viewModel.ingredientList.observe(viewLifecycleOwner) { ingredientList ->
            val pictureTopData = PictureResultType.Top(
                suggestion = getString(R.string.user_cocktail_recommendations, "홍길동"),
                ingredients = ingredientList
            )

            pictureTopAdapter.updateData(pictureTopData)
        }

        viewModel.ingredientAnalyzeCoctailList.observe(viewLifecycleOwner){
            val pictureBottomAdapter = PictureResultType.Bottom(
                cocktailCount = it.size.toString(),
                filters = listOf(),
                cocktails = it
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
            val cocktails: List<CocktailListResponse>
        ) : PictureResultType()
    }
}
