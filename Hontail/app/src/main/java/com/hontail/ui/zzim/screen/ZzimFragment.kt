package com.hontail.ui.zzim.screen

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.hontail.R
import com.hontail.base.BaseFragment
import com.hontail.data.model.response.CocktailListResponse
import com.hontail.data.model.response.LikedCocktail
import com.hontail.data.model.response.RecentViewedCocktail
import com.hontail.databinding.FragmentZzimBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.MainActivityViewModel
import com.hontail.ui.zzim.adapter.ZzimAdapter
import com.hontail.ui.zzim.viewmodel.ZzimViewModel
import com.hontail.util.CommonUtils

private const val TAG = "ZzimFragment_SSAFY"

class ZzimFragment: BaseFragment<FragmentZzimBinding>(
    FragmentZzimBinding::bind,
    R.layout.fragment_zzim
) {
    private lateinit var mainActivity: MainActivity

    private val activityViewModel: MainActivityViewModel by activityViewModels()
    private val viewModel: ZzimViewModel by viewModels()

    private lateinit var zzimAdapter: ZzimAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
        mainActivity.hideBottomNav(false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeCocktailComment()
        initAdapter()
        initEvent()
    }

    override fun onResume() {
        super.onResume()
        mainActivity.hideBottomNav(false)
    }

    // ViewModel Observe 등록
    private fun observeCocktailComment() {
        binding.apply {
            // 찜한 리스트
            viewModel.likedList.observe(viewLifecycleOwner) { likedList ->
                updateRecyclerView(likedList, viewModel.recentViewedList.value)
            }

            // 최근 본 리스트
            viewModel.recentViewedList.observe(viewLifecycleOwner) { recentViewedList ->
                Log.d(TAG, "Recent observeCocktailComment: ${recentViewedList}")
                updateRecyclerView(viewModel.likedList.value, recentViewedList)
            }

            // 최근 본 칵테일 ID 옵저버 (불필요한 호출 제거)
            viewModel.recentCoctailId.observe(viewLifecycleOwner) { newIds ->
                if (newIds != null && newIds.isNotEmpty()) {
                    Log.d(TAG, "Recent observeCocktailComment: ${newIds}")
                    viewModel.getLikedRecentViewed()
                }
            }
        }
    }

    // RecyclerView Adapter 연결
    private fun initAdapter() {
        binding.apply {
            zzimAdapter = ZzimAdapter(mainActivity, emptyList())

            recyclerViewZzim.layoutManager = LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false)
            recyclerViewZzim.adapter = zzimAdapter
        }
    }

    private fun initEvent(){
        zzimAdapter.zzimListListener = object : ZzimAdapter.ItemOnClickListener{
            override fun onClickCocktailItem(cocktailId: Int) {
                activityViewModel.setCocktailId(cocktailId)
                mainActivity.changeFragment(CommonUtils.MainFragmentName.COCKTAIL_DETAIL_FRAGMENT)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    mainActivity.changeFragment(CommonUtils.MainFragmentName.HOME_FRAGMENT)

                    mainActivity.binding.bottomNavigation.selectedItemId = R.id.navigation_home
                }
            })
    }

    // RecyclerView Update
    private fun updateRecyclerView(likedList: List<CocktailListResponse>?, recentList: List<CocktailListResponse>?) {
        val items = mutableListOf<ZzimItem>()

        // 찜한 리스트 여부에 따라 아이템 추가
        if (!likedList.isNullOrEmpty()) {
            items.add(ZzimItem.LikedList(likedList))
        } else {
            items.add(ZzimItem.Empty)
        }

        // 최근 본 리스트가 있다면 추가
        if (!recentList.isNullOrEmpty()) {
            items.add(ZzimItem.RecentViewedList(recentList))
        }

        zzimAdapter.updateItems(items)
    }

}

sealed class ZzimItem {
    data class LikedList(val likedList: List<CocktailListResponse>): ZzimItem()
    data class RecentViewedList(val recentList: List<CocktailListResponse>): ZzimItem()
    object Empty: ZzimItem()
}