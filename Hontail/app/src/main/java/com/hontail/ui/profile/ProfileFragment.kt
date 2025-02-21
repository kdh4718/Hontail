package com.hontail.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.hontail.R
import com.hontail.base.BaseFragment
import com.hontail.databinding.FragmentProfileBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.MainActivityViewModel
import com.hontail.ui.mypage.adapter.MyPageAdapter

class ProfileFragment : BaseFragment<FragmentProfileBinding>(
    FragmentProfileBinding::bind,
    R.layout.fragment_profile
) {
    private lateinit var mainActivity: MainActivity
    private val activityViewModel: MainActivityViewModel by activityViewModels()

    private lateinit var profileAdapter: ProfileAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainActivity.hideBottomNav(true)
        initToolbar()
        initAdapter()
    }

    // 툴바 설정
    private fun initToolbar() {

        binding.apply {

            toolbarProfile.apply {

                setNavigationIcon(R.drawable.go_back)
                setNavigationOnClickListener {
                    parentFragmentManager.popBackStack("ProfileFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                }
            }
        }
    }

    // 리사이클러뷰 어댑터 연결
    private fun initAdapter() {

        binding.apply {

            val items = mutableListOf<ProfileItem>().apply {
                add(ProfileItem.Profile("hyuun", 5))
                add(ProfileItem.Cocktail("깔루아 밀크", "리큐어", 2, 1231, 5))
                add(ProfileItem.Cocktail("에스프레소 마티니", "리큐어", 3, 1231, 22))
                add(ProfileItem.Cocktail("깔루아 콜라", "리큐어", 2, 1231, 16))
                add(ProfileItem.Cocktail("B-52", "리큐어", 5, 1231, 26))
            }

            val items2 = mutableListOf<ProfileItem>().apply {
                add(ProfileItem.Profile("hyuun", 5))
                if(isCocktailListEmpty()) {
                    add(ProfileItem.Empty)
                }
            }

            profileAdapter = ProfileAdapter(items)

            val layoutManager = GridLayoutManager(mainActivity, 2)

            layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when(profileAdapter.getItemViewType(position)) {
                        MyPageAdapter.VIEW_TYPE_PROFILE -> 2 // 프로필은 2개 영역을 차지
                        MyPageAdapter.VIEW_TYPE_COCKTAIL -> 1 // 칵테일 아이템은 1개 영역을 차지
                        MyPageAdapter.VIEW_TYPE_EMPTY -> 2 // 빈 영역 메시지는 2개 영역을 차지
                        else -> 1
                    }
                }
            }

            recyclerViewProfile.layoutManager = layoutManager
            recyclerViewProfile.adapter = profileAdapter
        }
    }

    // 레시피가 존재하는지 확인
    private fun isCocktailListEmpty(): Boolean {
        return true
    }
}

sealed class ProfileItem {

    data class Profile(val userName: String, val recipeCnt: Int): ProfileItem()
    data class Cocktail(val cocktailName: String, val cocktailBaseSpirit: String, val cocktailIngredientCnt: Int, val cocktailZzimCnt: Int, val cocktailAlcholContent: Int): ProfileItem()
    object Empty: ProfileItem()
}