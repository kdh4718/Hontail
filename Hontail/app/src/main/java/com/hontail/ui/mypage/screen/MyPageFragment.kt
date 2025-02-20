package com.hontail.ui.mypage.screen

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.hontail.R
import com.hontail.base.ApplicationClass
import com.hontail.base.BaseFragment
import com.hontail.data.model.response.CocktailListResponse
import com.hontail.data.model.response.MyPageInformationResponse
import com.hontail.databinding.FragmentMyPageBinding
import com.hontail.ui.LoginActivity
import com.hontail.ui.MainActivity
import com.hontail.ui.MainActivityViewModel
import com.hontail.ui.mypage.adapter.MyPageAdapter
import com.hontail.ui.mypage.viewmodel.MyPageViewModel
import com.hontail.util.CommonUtils

private const val TAG = "MyPageFragment"
class MyPageFragment : BaseFragment<FragmentMyPageBinding>(
    FragmentMyPageBinding::bind,
    R.layout.fragment_my_page
) {
    private lateinit var mainActivity: MainActivity
    private lateinit var loginActivity: LoginActivity

    private val activityViewModel: MainActivityViewModel by activityViewModels()
    private val viewModel: MyPageViewModel by viewModels()

    private lateinit var myPageAdapter: MyPageAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainActivity.hideBottomNav(false)  // 하단바 다시 보이게 설정

        observeMyPage()
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

    // ViewModel Observe 등록
    private fun observeMyPage() {

        binding.apply {

            viewModel.userInfo.observe(viewLifecycleOwner) { userInfo ->
                Log.d(TAG, "observeMyPage: userInfo 업데이트 감지됨 -> $userInfo")

                val cocktailCnt = viewModel.cocktailList.value?.size ?: 0
                val currentItems = myPageAdapter.items.toMutableList()
                activityViewModel.userNickname = userInfo.user_nickname

                userInfo?.let {
                    val profileItem = MyPageItem.Profile(it, cocktailCnt)

                    // ✅ 기존 리스트에서 Profile을 유지하면서 갱신
                    val updatedItems = currentItems.filterNot { it is MyPageItem.Profile }.toMutableList()
                    updatedItems.add(0, profileItem) // 항상 첫 번째에 Profile 추가

                    Log.d(TAG, "observeMyPage: updatedItems 리스트 -> $updatedItems")

                    myPageAdapter.updateItems(updatedItems)
                }
            }

            viewModel.cocktailList.observe(viewLifecycleOwner) { cocktails ->
                Log.d(TAG, "observeMyPage: 칵테일 리스트 업데이트 감지됨 -> ${cocktails.size} 개")

                val currentItems = myPageAdapter.items.toMutableList()

                val userInfo = viewModel.userInfo.value
                val cocktailCount = cocktails.size

                userInfo?.let {
                    val profileItem = MyPageItem.Profile(it, cocktailCount)

                    // ✅ 기존 리스트에서 Profile을 유지하면서 갱신
                    val updatedItems = currentItems.filterNot { it is MyPageItem.Profile }.toMutableList()
                    updatedItems.add(0, profileItem) // 항상 첫 번째에 Profile 추가

                    if (cocktails.isNotEmpty()) {
                        updatedItems.add(MyPageItem.MyCocktail(cocktails))
                    } else {
                        Log.d(TAG, "observeMyPage: 칵테일 없음 -> Empty 아이템 추가")
                        updatedItems.add(MyPageItem.Empty(userInfo))
                    }

                    Log.d(TAG, "observeMyPage: 최종 updatedItems 리스트 -> $updatedItems")

                    myPageAdapter.updateItems(updatedItems)
                }
            }

        }
    }


    // 리사이클러뷰 어댑터 연결
    private fun initAdapter() {

        binding.apply {

            myPageAdapter = MyPageAdapter(mainActivity, emptyList())

            recyclerViewMyPage.layoutManager = LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false)
            recyclerViewMyPage.adapter = myPageAdapter
        }
    }

    // 이벤트
    private fun initEvent() {

        binding.apply {

            myPageAdapter.myPageListener = object : MyPageAdapter.ItemOnClickListener {

                // 상세 화면으로 가기.
                override fun onClickToCocktailDetail(cocktailId: Int) {
                    activityViewModel.setCocktailId(cocktailId)
                    mainActivity.changeFragment(CommonUtils.MainFragmentName.COCKTAIL_DETAIL_FRAGMENT)
                }

                // 프로필 관리
                override fun onClickManageProfile() {
                    mainActivity.changeFragment(CommonUtils.MainFragmentName.MY_PAGE_MODIFY_FRAGMENT)
                }

                // 재료 요청
                override fun onClickRequestIngredient() {
                    mainActivity.changeFragment(CommonUtils.MainFragmentName.INGREDIENT_ADD_FRAGMENT)
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
    }

}

sealed class MyPageItem {

    data class Profile(val userInfo: MyPageInformationResponse, val cocktailCnt: Int) : MyPageItem()
    data class MyCocktail(val cocktailList: List<CocktailListResponse>) : MyPageItem()
    data class Empty(val userInfo: MyPageInformationResponse) : MyPageItem()
}