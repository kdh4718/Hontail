package com.hontail.ui.custom

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.hontail.R
import com.hontail.base.BaseFragment
import com.hontail.databinding.FragmentCustomCocktailSearchBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.MainActivityViewModel
import com.hontail.util.CommonUtils

private const val TAG = "CustomCocktailSearchFra"
class CustomCocktailSearchFragment: BaseFragment<FragmentCustomCocktailSearchBinding>(
    FragmentCustomCocktailSearchBinding::bind,
    R.layout.fragment_custom_cocktail_search
) {

    private lateinit var mainActivity: MainActivity

    private val activityViewModel: MainActivityViewModel by activityViewModels()
    private val viewModel: CustomCocktailViewModel by viewModels()

    private lateinit var customCocktailSearchAdapter: CustomCocktailSearchAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeSearchResult()
        initAdapter()
        initEvent()
    }

    // ViewModel의 검색 결과 관찰
    private fun observeSearchResult() {

        binding.apply {

            viewModel.searchResults.observe(viewLifecycleOwner) { ingredients ->

                Log.d(TAG, "observeSearchResult: ${ingredients.size}")

                val newItems = mutableListOf<CustomCocktailSearchItem>()

                val currentQuery = viewModel.searchQuery.value.orEmpty()
                newItems.add(CustomCocktailSearchItem.SearchBar(currentQuery))

                if(ingredients.isNotEmpty()) {
                    newItems.add(CustomCocktailSearchItem.Header)
                    ingredients.forEach { ingredient ->
                        newItems.add(
                            CustomCocktailSearchItem.IngredientResult(
                                ingredient.ingredientId,
                                ingredient.ingredientNameKor,
                                ingredient.ingredientImage
                            )
                        )
                    }
                }
                else {
                    Toast.makeText(mainActivity, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()
                }

                customCocktailSearchAdapter.updateItems(newItems)
            }
        }
    }

    // 리사이클러뷰 어댑터 연결
    private fun initAdapter() {

        binding.apply {

//            val items = mutableListOf<CustomCocktailSearchItem>().apply {
//                add(CustomCocktailSearchItem.SearchBar("오렌지"))
//                add(CustomCocktailSearchItem.Header)
//                add(CustomCocktailSearchItem.IngredientResult("오렌지"))
//                add(CustomCocktailSearchItem.IngredientResult("오렌지 버터"))
//                add(CustomCocktailSearchItem.IngredientResult("오렌지 플라워 워터"))
//            }
//
//            val items2 = mutableListOf<CustomCocktailSearchItem>().apply {
//                add(CustomCocktailSearchItem.SearchBar(null))
//            }

            // 초기 아이템.
            val initialItems = mutableListOf<CustomCocktailSearchItem>(
                CustomCocktailSearchItem.SearchBar(null)
            )

            customCocktailSearchAdapter = CustomCocktailSearchAdapter(initialItems)

            recyclerViewCustomCocktailSearch.layoutManager = LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false)
            recyclerViewCustomCocktailSearch.adapter = customCocktailSearchAdapter
        }
    }

    // 이벤트
    private fun initEvent() {

        binding.apply {

            customCocktailSearchAdapter.customCocktailSearchListener = object : CustomCocktailSearchAdapter.ItemOnClickListener {

                // 재료 선택
                override fun onClickIngredient(ingredientId: Int) {
                    activityViewModel.setIngredientId(ingredientId)
                    mainActivity.changeFragment(CommonUtils.MainFragmentName.CUSTOM_COCKTAIL_INGREDIENT_DETAIL_FRAGMENT)
                }

                // 취소
                override fun onClickCancel() {
                    parentFragmentManager.popBackStack("CustomCocktailSearchFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                }
            }

            customCocktailSearchAdapter.onSearchQueryChanged = { query ->
                Log.d(TAG, "initEvent: $query")
                viewModel.setSearchQuery(query)
            }
        }
    }

}

sealed class CustomCocktailSearchItem {

    data class SearchBar(val query: String?): CustomCocktailSearchItem()
    object Header: CustomCocktailSearchItem()
    data class IngredientResult(val ingredientId: Int, val ingredientName: String, val ingredientImage: String): CustomCocktailSearchItem()
}