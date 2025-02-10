package com.hontail.ui.cocktail.screen

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.hontail.R
import com.hontail.base.BaseFragment
import com.hontail.databinding.FragmentCocktailDetailBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.MainActivityViewModel
import com.hontail.ui.cocktail.adapter.CocktailDetailAdapter
import com.hontail.ui.ingredient.Ingredient

class CocktailDetailFragment : BaseFragment<FragmentCocktailDetailBinding>(
    FragmentCocktailDetailBinding::bind,
    R.layout.fragment_cocktail_detail
) {
    private lateinit var mainActivity: MainActivity
    private val activityViewModel: MainActivityViewModel by activityViewModels()

    private lateinit var cocktailDetailAdapter: CocktailDetailAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainActivity.hideBottomNav(true)  // 하단바 안보이게 설정
        initAdapter()
        initEvent()
    }

    // 리사이클러뷰 어댑터 연결
    private fun initAdapter() {
        binding.apply {
            val ingredients = mutableListOf<Ingredient>().apply {
                add(Ingredient("슈퍼파인 슈가", "1 T"))
                add(Ingredient("레몬", "1/2 개"))
                add(Ingredient("오렌지", "1/2 개"))
                add(Ingredient("팔로 코르타도", "45 ml"))
                add(Ingredient("아몬틸라도 쉐리", "45 ml"))
            }

            val recipes = mutableListOf<Recipe>().apply {
                add(Recipe(1, "쉐이커에 슈퍼파인 슈가를 넣고 레몬과 오렌지 슬라이스를 추가합니다."))
                add(Recipe(2, "머들러(으깨는 도구)를 사용해 과일과 설탕을 잘 으깨 재료의 향과 과즙을 추출합니다."))
                add(Recipe(3, "팔로 코르타도와 아문틸라도 쉐리를 쉐이커에 부어줍니다."))
                add(Recipe(4, "크러시드 아이스를 쉐이커에 가득 채운 후 강하게 흔들어줍니다."))
                add(Recipe(5, "잔에 크러시드 아이스를 채운 뒤 쉐이커 내용을 체로 걸러 부어줍니다."))
                add(Recipe(6, "민트 잎과 오렌지 슬라이스로 장식하여 완성합니다."))
            }

            val items = mutableListOf<CocktailDetailItem>().apply {
                add(
                    CocktailDetailItem.CocktailInfo(
                        null,
                        "쉐리 코블러",
                        "Sherry cobDbler",
                        15,
                        383,
                        3,
                        "아몬틸라도 쉐리와 팔로 코르타도가 어우러진 고전적이고 상쾌한 맛의 칵테일입니다."
                    )
                )
                add(CocktailDetailItem.IngredientList(ingredients))
                add(CocktailDetailItem.RecipeList(recipes))
            }

            cocktailDetailAdapter = CocktailDetailAdapter(mainActivity, items)

            recyclerViewCocktailDetail.layoutManager = LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false)
            recyclerViewCocktailDetail.adapter = cocktailDetailAdapter
        }
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
    data class CocktailInfo(
        val imageUrl: String?,
        val cocktailName: String,
        val cocktailNameEn: String,
        val cocktailAlcoholLevel: Int,
        val cocktailZzims: Int,
        val cocktailComments: Int,
        val cocktailDescription: String
    ): CocktailDetailItem()

    data class IngredientList(val ingredients: List<Ingredient>): CocktailDetailItem()

    data class RecipeList(val recipes: List<Recipe>): CocktailDetailItem()
}

data class Recipe(val recipeNumber: Int, val recipeContent: String)