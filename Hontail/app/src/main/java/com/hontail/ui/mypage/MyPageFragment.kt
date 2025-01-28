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

            myPageCocktailAdapter = MyPageCocktailAdapter()

            recyclerViewMyPage.adapter = myPageCocktailAdapter
            recyclerViewMyPage.layoutManager = GridLayoutManager(mainActivity, 2)
        }
    }
}