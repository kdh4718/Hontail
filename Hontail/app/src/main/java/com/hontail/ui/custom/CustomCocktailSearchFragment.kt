package com.hontail.ui.custom

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.hontail.R
import com.hontail.base.BaseFragment
import com.hontail.databinding.FragmentCustomCocktailSearchBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.MainActivityViewModel

class CustomCocktailSearchFragment: BaseFragment<FragmentCustomCocktailSearchBinding>(
    FragmentCustomCocktailSearchBinding::bind,
    R.layout.fragment_custom_cocktail_search
) {

    private lateinit var mainActivity: MainActivity

    private val activityViewModel: MainActivityViewModel by activityViewModels()

    private lateinit var customCocktailSearchAdapter: CustomCocktailSearchAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        initEvent()
    }

    // 리사이클러뷰 어댑터 연결
    private fun initAdapter() {

        binding.apply {

            val items = mutableListOf<CustomCocktailSearchItem>().apply {
                add(CustomCocktailSearchItem.SearchBar("오렌지"))
                add(CustomCocktailSearchItem.Header)
                add(CustomCocktailSearchItem.IngredientResult("오렌지"))
                add(CustomCocktailSearchItem.IngredientResult("오렌지 버터"))
                add(CustomCocktailSearchItem.IngredientResult("오렌지 플라워 워터"))
            }

            val items2 = mutableListOf<CustomCocktailSearchItem>().apply {
                add(CustomCocktailSearchItem.SearchBar(null))
            }

            customCocktailSearchAdapter = CustomCocktailSearchAdapter(items)

            recyclerViewCustomCocktailSearch.layoutManager = LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false)
            recyclerViewCustomCocktailSearch.adapter = customCocktailSearchAdapter
        }
    }

    // 이벤트
    private fun initEvent() {

        binding.apply {

            // 취소
            customCocktailSearchAdapter.customCocktailSearchCancelListener = object : CustomCocktailSearchAdapter.ItemOnClickListener {
                override fun onClick(position: Int?) {
                    parentFragmentManager.popBackStack("CustomCocktailSearchFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                }
            }

            // 재료 선택
            customCocktailSearchAdapter.customCocktailSearchIngredientListener = object : CustomCocktailSearchAdapter.ItemOnClickListener {
                override fun onClick(position: Int?) {

                }
            }

            // 재료 삭제
            customCocktailSearchAdapter.customCocktailSearchDeleteListener = object : CustomCocktailSearchAdapter.ItemOnClickListener {
                override fun onClick(position: Int?) {
                    customCocktailSearchAdapter.notifyItemRemoved(position!!)
                }
            }
        }
    }

}

sealed class CustomCocktailSearchItem {

    data class SearchBar(val query: String?): CustomCocktailSearchItem()
    object Header: CustomCocktailSearchItem()
    data class IngredientResult(val ingredientName: String): CustomCocktailSearchItem()
}