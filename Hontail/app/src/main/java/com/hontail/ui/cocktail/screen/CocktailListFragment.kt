package com.hontail.ui.cocktail.screen

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.asFlow
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hontail.R
import com.hontail.base.BaseFragment
import com.hontail.data.model.response.CocktailListResponse
import com.hontail.databinding.FragmentCocktailListBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.MainActivityViewModel
import com.hontail.ui.cocktail.viewmodel.CocktailListFragmentViewModel
import com.hontail.ui.cocktail.adapter.CocktailListAdapter
import com.hontail.ui.picture.FilterBottomSheetFragment
import com.hontail.util.CommonUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest

private const val TAG = "CocktailListFragment_SSAFY"

class CocktailListFragment : BaseFragment<FragmentCocktailListBinding>(
    FragmentCocktailListBinding::bind,
    R.layout.fragment_cocktail_list
) {
    private lateinit var mainActivity: MainActivity
    private val activityViewModel: MainActivityViewModel by activityViewModels()
    private val viewModel: CocktailListFragmentViewModel by viewModels()
    private val filters = listOf("찜", "시간", "도수", "베이스주")

    private lateinit var cocktailListAdapter: CocktailListAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.baseSpirit = activityViewModel.baseSpirit.value!!
        viewModel.setUserId(activityViewModel.userId)
    }

    override fun onResume() {
        super.onResume()
        mainActivity.hideBottomNav(false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity.hideBottomNav(false)
        initAdapter()
        initData()
        initEvent()
    }

    private fun initAdapter() {
        binding.apply {
            // 초기 Adapter 설정
            cocktailListAdapter = CocktailListAdapter(mainActivity, mutableListOf(), viewLifecycleOwner)
            recyclerViewCocktailList.layoutManager = LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false)
            recyclerViewCocktailList.adapter = cocktailListAdapter
        }
    }

    private fun initData() {
        lifecycleScope.launch {
            viewModel.pagedCocktailList.collect { pagingData ->
                cocktailListAdapter.submitData(pagingData)
            }
        }

//        // 스크롤 리스너 설정
//        binding.recyclerViewCocktailList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//
//                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
//                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
//                val totalItemCount = layoutManager.itemCount
//
//                // 페이지 끝에 도달하면 다음 페이지를 로드
//                if (lastVisibleItem == totalItemCount - 1) {
//                    Log.d(TAG, "리스트 끝에 도달, 다음 페이지 로딩 시작")
//                    // 추가 페이지 로드를 위한 작업
//                    cocktailListAdapter.retry()  // Paging의 loadState를 활용하여 다음 페이지 요청
//                }
//            }
//        })
    }

    private fun initEvent() {
        binding.apply {
            cocktailListAdapter.cocktailListListener = object : CocktailListAdapter.ItemOnClickListener {
                override fun onClickRandom() {
                    val dialog = CocktailRandomDialogFragment()
                    dialog.show(parentFragmentManager, "CocktailRandomDialog")
                }

                override fun onClickCocktailItem(cocktailId: Int) {
                    activityViewModel.setCocktailId(cocktailId)
                    mainActivity.changeFragment(CommonUtils.MainFragmentName.COCKTAIL_DETAIL_FRAGMENT)
                }

                override fun onClickSearch() {
                    mainActivity.changeFragment(CommonUtils.MainFragmentName.COCKTAIL_SEARCH_FRAGMENT)
                }

                override fun onClickTab(position: Int) {
                    viewModel.isCustom = (position == 1)
                    viewModel.getCocktailFiltering()
                }

                override fun onClickFilter(position: Int) {
                    val bottomSheetFragment = FilterBottomSheetFragment.newInstance(position)
                    bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
                }
            }
        }
    }
}

sealed class CocktailListItem {
    object SearchBar : CocktailListItem()
    object TabLayout : CocktailListItem()
    data class Filter(val filters: List<String>) : CocktailListItem()
    data class CocktailItems(val cocktails: PagingData<CocktailListResponse>) : CocktailListItem()
}