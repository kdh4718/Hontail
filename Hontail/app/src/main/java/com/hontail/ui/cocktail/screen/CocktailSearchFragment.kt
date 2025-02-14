package com.hontail.ui.cocktail.screen

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.hontail.R
import com.hontail.base.BaseFragment
import com.hontail.data.model.dto.SearchHistoryTable
import com.hontail.data.model.response.CocktailListResponse
import com.hontail.databinding.FragmentCocktailSearchBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.MainActivityViewModel
import com.hontail.ui.cocktail.adapter.CocktailSearchAdapter
import com.hontail.ui.cocktail.viewmodel.CocktailSearchFragmentViewModel
import com.hontail.util.CommonUtils

private const val TAG = "CocktailSearchFragment_SSAFY"

class CocktailSearchFragment : BaseFragment<FragmentCocktailSearchBinding>(
    FragmentCocktailSearchBinding::bind,
    R.layout.fragment_cocktail_search
) {
    private lateinit var mainActivity: MainActivity
    private val activityViewModel: MainActivityViewModel by activityViewModels()
    private val viewModel: CocktailSearchFragmentViewModel by viewModels()

    private lateinit var cocktailSearchAdapter: CocktailSearchAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadSearchHistory()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        initData()
        initEvent()
    }

    // 리사이클러뷰 어댑터 연결
    private fun initAdapter() {
        binding.apply {
            val items = mutableListOf<CocktailSearchItem>().apply {
                add(CocktailSearchItem.SearchBar(null))
                add(CocktailSearchItem.Recent(emptyList()))
            }

            cocktailSearchAdapter = CocktailSearchAdapter(mainActivity, items)

            recyclerViewCocktailSearch.layoutManager = LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false)
            recyclerViewCocktailSearch.adapter = cocktailSearchAdapter
        }
    }

    fun initData(){
        viewModel.searchHistoryList.observe(viewLifecycleOwner){
            Log.d(TAG, "initData Search: ${it}")
            val updatedItems = mutableListOf<CocktailSearchItem>().apply {
                add(CocktailSearchItem.SearchBar(null))
                add(CocktailSearchItem.Recent(it))
            }

            cocktailSearchAdapter.updateItems(updatedItems)
        }

        viewModel.cocktailList.observe(viewLifecycleOwner){
            Log.d(TAG, "initData Cocktail: ${it}")
            val updatedItems = mutableListOf<CocktailSearchItem>().apply {
                add(CocktailSearchItem.SearchBar(null))
                add(CocktailSearchItem.Result(it))
            }

            cocktailSearchAdapter.updateItems(updatedItems)
        }
    }

    // 이벤트
    private fun initEvent() {
        binding.apply {
            cocktailSearchAdapter.cocktailSearchListener = object : CocktailSearchAdapter.ItemOnClickListener {
                // 취소
                override fun onClickCancel() {
                    parentFragmentManager.popBackStack("CocktailSearchFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                }

                override fun onClickSearch(text: String) {
                    viewModel.getCocktailByName(text)
                    viewModel.insertSearchHistory(text)
                }

                override fun onClickCocktailItem(cocktailId: Int) {
                    activityViewModel.setCocktailId(cocktailId)
                    mainActivity.changeFragment(CommonUtils.MainFragmentName.COCKTAIL_DETAIL_FRAGMENT)
                }

                override fun onClickSearchHistoryDelete(id: Int) {
                    viewModel.deleteSearchHistory(id)
                }
            }
        }
    }
}

sealed class CocktailSearchItem {
    data class SearchBar(val query: String?): CocktailSearchItem()
    data class Recent(val recentList: List<SearchHistoryTable>): CocktailSearchItem()
    data class Result(val resultList: List<CocktailListResponse>): CocktailSearchItem()
}