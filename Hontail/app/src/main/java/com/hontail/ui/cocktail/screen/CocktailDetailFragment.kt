package com.hontail.ui.cocktail.screen

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.hontail.R
import com.hontail.base.BaseFragment
import com.hontail.data.model.response.CocktailDetailResponse
import com.hontail.data.model.response.Recipe
import com.hontail.databinding.FragmentCocktailDetailBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.MainActivityViewModel
import com.hontail.ui.cocktail.adapter.CocktailDetailAdapter
import com.hontail.ui.cocktail.viewmodel.CocktailDetailFragmentViewModel

class CocktailDetailFragment : BaseFragment<FragmentCocktailDetailBinding>(
    FragmentCocktailDetailBinding::bind,
    R.layout.fragment_cocktail_detail
) {
    private lateinit var mainActivity: MainActivity
    private val activityViewModel: MainActivityViewModel by activityViewModels()
    private val viewModel: CocktailDetailFragmentViewModel by viewModels()

    private lateinit var cocktailDetailAdapter: CocktailDetailAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.cocktailId = activityViewModel.cocktailId.value ?: 0
        viewModel.userId = activityViewModel.userId

        viewModel.getCocktailDetailInfo()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getCocktailDetailInfo()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainActivity.hideBottomNav(true)  // 하단바 안보이게 설정
        initAdapter()
        initData()
        initEvent()
    }

    fun initData(){
        viewModel.cocktailInfo.observe(viewLifecycleOwner) { cocktailDetail ->
            updateAdapterData(cocktailDetail)
        }
    }

    // 리사이클러뷰 어댑터 연결
    private fun initAdapter() {
        binding.apply {
            cocktailDetailAdapter = CocktailDetailAdapter(mainActivity, mutableListOf())
            recyclerViewCocktailDetail.layoutManager = LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false)
            recyclerViewCocktailDetail.adapter = cocktailDetailAdapter
        }
    }

    // 어댑터에 새로운 데이터를 반영하는 메서드
    private fun updateAdapterData(cocktailDetail: CocktailDetailResponse) {
        val items = mutableListOf<CocktailDetailItem>().apply {
            add(CocktailDetailItem.CocktailInfo(cocktailDetail)) // 전체 객체를 넘김
            add(CocktailDetailItem.IngredientList(cocktailDetail.cocktailIngredients))
            add(CocktailDetailItem.RecipeList(cocktailDetail.recipes))
        }
        cocktailDetailAdapter.updateItems(items)
    }


    fun initEvent() {
        binding.apply {
            // 뒤로가기 버튼 클릭 리스너 추가
            cocktailDetailAdapter.setBackButtonClickListener {
                mainActivity.onBackPressed()
            }

            cocktailDetailAdapter.cocktailDetailListener = object : CocktailDetailAdapter.ItemOnClickListener {
                // 레시피 쿠킹모드 바텀 시트
                override fun onClickRecipeBottomSheet() {
                    val bottomSheet = CocktailCookBottomSheetFragment()
                    bottomSheet.show(parentFragmentManager, bottomSheet.tag)
                }

                // 댓글 바텀 시트
                override fun onClickCommentBottomSheet() {
                    val bottomSheet = CocktailCommentBottomSheetFragment()
                    bottomSheet.show(parentFragmentManager, bottomSheet.tag)
                }
            }
        }
    }
}

sealed class CocktailDetailItem {
    data class CocktailInfo(val cocktailDetail: CocktailDetailResponse) : CocktailDetailItem()
    data class IngredientList(val ingredients: List<com.hontail.data.model.response.CocktailIngredient>): CocktailDetailItem()
    data class RecipeList(val recipes: List<Recipe>): CocktailDetailItem()
}