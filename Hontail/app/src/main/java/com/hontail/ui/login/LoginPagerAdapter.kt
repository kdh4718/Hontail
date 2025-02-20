package com.hontail.ui.login

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hontail.R

class LoginPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    private val pages = listOf(
        LoginPageFragment.newInstance(R.drawable.login1, "그럴 때 있지 않나요?", "술이 한 잔 생각나는 밤이요"),
        LoginPageFragment.newInstance(R.drawable.login2, "매일 먹는 소주 대신", "특별한 칵테일 한 잔은 어떤가요?"),
        LoginPageFragment.newInstance(R.drawable.login3, "오늘 하루가 쓴 당신을 위해", "달달한 선물 하나 준비했어요 :)")
    )

    override fun getItemCount(): Int = pages.size

    override fun createFragment(position: Int): Fragment = pages[position]
}
