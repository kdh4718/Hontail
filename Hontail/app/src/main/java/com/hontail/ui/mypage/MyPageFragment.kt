package com.hontail.ui.mypage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.hontail.R
import com.hontail.base.BaseFragment
import com.hontail.databinding.FragmentMyPageBinding
import com.hontail.ui.LoginActivity
import com.hontail.ui.MainActivity
import com.hontail.ui.MainActivityViewModel
import com.hontail.util.CommonUtils


class MyPageFragment : BaseFragment<FragmentMyPageBinding>(
    FragmentMyPageBinding::bind,
    R.layout.fragment_my_page
) {
    private lateinit var mainActivity: MainActivity
    private lateinit var loginActivity: LoginActivity
    private val activityViewModel: MainActivityViewModel by activityViewModels()

    private lateinit var myPageCocktailAdapter: MyPageCocktailAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolbar()
        initAdapter()
        initEvent()
    }

    // 툴바 설정
    private fun initToolbar() {

        binding.apply {

            toolbarMyPage.apply {

                inflateMenu(R.menu.menu_mypage)

                setOnMenuItemClickListener {

                    when(it.itemId) {

                        // 로그아웃
                        R.id.menu_mypage_logout -> {
                            logout()
                        }
                    }
                    true
                }
            }
        }
    }

    // 로그아웃
    private fun logout() {

        mainActivity.finish()

        val intent = Intent(mainActivity, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        startActivity(intent)
    }

    // 리사이클러뷰 어댑터 연결
    private fun initAdapter() {

        binding.apply {

            val items = mutableListOf<MyPageItem>().apply {
                add(MyPageItem.Profile("hyuun", 5))
                add(MyPageItem.Cocktail("깔루아 밀크", "리큐어", 2, 1231, 5))
                add(MyPageItem.Cocktail("에스프레소 마티니", "리큐어", 3, 1231, 22))
                add(MyPageItem.Cocktail("깔루아 콜라", "리큐어", 2, 1231, 16))
                add(MyPageItem.Cocktail("B-52", "리큐어", 5, 1231, 26))
            }

            val items2 = mutableListOf<MyPageItem>().apply {
                add(MyPageItem.Profile("hyuun", 5))
                if(isCocktailListEmpty()) {
                    add(MyPageItem.Empty)
                }
            }

            myPageCocktailAdapter = MyPageCocktailAdapter(items)

            val layoutManager = GridLayoutManager(mainActivity, 2)

            layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when(myPageCocktailAdapter.getItemViewType(position)) {
                        MyPageCocktailAdapter.VIEW_TYPE_PROFILE -> 2 // 프로필은 2개 영역을 차지
                        MyPageCocktailAdapter.VIEW_TYPE_COCKTAIL -> 1 // 칵테일 아이템은 1개 영역을 차지
                        MyPageCocktailAdapter.VIEW_TYPE_EMPTY -> 2 // 빈 영역 메시지는 2개 영역을 차지
                        else -> 1
                    }
                }
            }

            recyclerViewMyPage.layoutManager = layoutManager
            recyclerViewMyPage.adapter = myPageCocktailAdapter
        }
    }

    // 이벤트
    private fun initEvent() {

        binding.apply {

            // 프로필 관리
            myPageCocktailAdapter.myPageProfileListener = object : MyPageCocktailAdapter.ItemOnClickListener {
                override fun onClick() {
                    mainActivity.changeFragment(CommonUtils.MainFragmentName.MY_PAGE_MODIFY_FRAGMENT)
                }
            }

            // 재료 요청
            myPageCocktailAdapter.myPageIngredientListener = object : MyPageCocktailAdapter.ItemOnClickListener {
                override fun onClick() {
                    TODO("Not yet implemented")
                }
            }
        }
    }

    // 레시피가 존재하는지 확인
    private fun isCocktailListEmpty(): Boolean {
        return true
    }
}

sealed class MyPageItem {

    data class Profile(val userName: String, val recipeCnt: Int): MyPageItem()
    data class Cocktail(val cocktailName: String, val cocktailBaseSpirit: String, val cocktailIngredientCnt: Int, val cocktailZzimCnt: Int, val cocktailAlcholContent: Int): MyPageItem()
    object Empty: MyPageItem()
}