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
import com.hontail.data.model.response.CocktailListResponse
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

    private lateinit var myPageAdapter: MyPageAdapter

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

                    when (it.itemId) {

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

            val cocktailList = mutableListOf<CocktailListResponse>().apply {
                add(
                    CocktailListResponse(
                        1, "깔루아 밀크", "https://cdn.diffords.com/contrib/stock-images/2016/7/30/20168fcf1a85da47c9369831cca42ee82d33.jpg", 1231, 12, "",
                        "2025-01-27 00:13:32", 5, false
                    )
                )
                add(
                    CocktailListResponse(
                        2,
                        "에스프레소 마티니",
                        "https://cdn.diffords.com/contrib/stock-images/2016/7/30/20168fcf1a85da47c9369831cca42ee82d33.jpg",
                        0,
                        0,
                        "리큐어",
                        "2025-01-27 00:13:32",
                        3,
                        true
                    )
                )
            }

            val items = mutableListOf<MyPageItem>().apply {
                add(MyPageItem.Profile("hyuun", 5))
                add(MyPageItem.MyCocktail(cocktailList))
            }

            val items2 = mutableListOf<MyPageItem>().apply {
                add(MyPageItem.Profile("hyuun", 5))
                if (isCocktailListEmpty()) {
                    add(MyPageItem.Empty)
                }
            }

            myPageAdapter = MyPageAdapter(mainActivity, items)

            recyclerViewMyPage.layoutManager = LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false)
            recyclerViewMyPage.adapter = myPageAdapter
        }
    }

    // 이벤트
    private fun initEvent() {

        binding.apply {

            // 프로필 관리
            myPageAdapter.myPageProfileListener = object : MyPageAdapter.ItemOnClickListener {
                override fun onClick() {
                    mainActivity.changeFragment(CommonUtils.MainFragmentName.MY_PAGE_MODIFY_FRAGMENT)
                }
            }

            // 재료 요청
            myPageAdapter.myPageIngredientListener = object : MyPageAdapter.ItemOnClickListener {
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

    data class Profile(val userName: String, val recipeCnt: Int) : MyPageItem()
    data class MyCocktail(val cocktailList: List<CocktailListResponse>) : MyPageItem()
    object Empty : MyPageItem()
}