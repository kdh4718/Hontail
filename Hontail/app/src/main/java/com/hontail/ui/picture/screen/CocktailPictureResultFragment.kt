package com.hontail.ui.picture.screen

import android.content.Context
import android.os.Bundle
import android.util.Log
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
import com.hontail.util.CommonUtils

private const val TAG = "CocktailPictureResultFr_SSAFY"

class CocktailPictureResultFragment : BaseFragment<FragmentCocktailPictureResultBinding>(
    FragmentCocktailPictureResultBinding::bind,
    R.layout.fragment_cocktail_picture_result
) {
    private lateinit var mainActivity: MainActivity
    private val activityViewModel: MainActivityViewModel by activityViewModels()
    private val viewModel: CocktailPictureResultFragmentViewModel by viewModels()

    private lateinit var pictureTopAdapter: PictureTopAdapter
    private lateinit var pictureBottomAdapter: PictureBottomAdapter

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
        initEvent()
        setupBackButton()
    }

    private fun setupBackButton() {
        binding.imageViewPictureResultGoBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    fun initEvent() {
        pictureBottomAdapter.pictureBottomListener =
            object : PictureBottomAdapter.ItemClickListener {
                override fun onClickCocktailItem(cocktailId: Int) {
                    activityViewModel.setCocktailId(cocktailId)
                    mainActivity.changeFragment(CommonUtils.MainFragmentName.COCKTAIL_DETAIL_FRAGMENT)
                }

            }

        viewModel.userInfo.observe(viewLifecycleOwner) {
            viewModel.setUserNickname(it.user_nickname)
        }

        viewModel.userNickname.observe(viewLifecycleOwner) {
            Log.d(TAG, "userNickname observe: ${it}")

            val pictureTopData = PictureResultType.Top(
                suggestion = listOf(
                    getString(
                        R.string.user_cocktail_recommendations,
                        it
                    ), it
                ),
                ingredients = emptyList()
            )

            pictureTopAdapter.updateData(pictureTopData)
        }
    }

    fun initData() {
        viewModel.getIngredientAnalyze()
    }

    private fun initRecyclerView() {
        binding.recyclerViewPictureResult.layoutManager = LinearLayoutManager(requireContext())

        pictureTopAdapter = PictureTopAdapter(
            requireContext(),
            PictureResultType.Top(
                listOf(
                    getString(
                        R.string.user_cocktail_recommendations,
                        viewModel.userNickname.value
                    ), viewModel.userNickname.value ?: ""
                ), emptyList()
            )
        )
        pictureBottomAdapter =
            PictureBottomAdapter(requireContext(), PictureResultType.Bottom("", emptyList()))

        // 어댑터를 ConcatAdapter로 묶기
        val concatAdapter = ConcatAdapter(pictureTopAdapter, pictureBottomAdapter)
        binding.recyclerViewPictureResult.adapter = concatAdapter

        viewModel.ingredientAnalyzeCoctailList.observe(viewLifecycleOwner) {
            Log.d(TAG, "AIAIAIAIAIAIAIAIinitRecyclerView: ${it}")
            val pictureBottomData = PictureResultType.Bottom(
                cocktailCount = it.size.toString(),
                cocktails = it
            )

            pictureBottomAdapter.updateData(pictureBottomData)

            val pictureTopData = PictureResultType.Top(
                suggestion = listOf(
                    getString(
                        R.string.user_cocktail_recommendations,
                        viewModel.userNickname.value
                    ), viewModel.userNickname.value!!
                ),
                ingredients = emptyList()
            )

            pictureTopAdapter.updateData(pictureTopData)
        }
    }

    sealed class PictureResultType {
        data class Top(
            val suggestion: List<String>,
            val ingredients: List<String>
        ) : PictureResultType()

        data class Bottom(
            val cocktailCount: String,
            val cocktails: List<CocktailListResponse>
        ) : PictureResultType()
    }
}
