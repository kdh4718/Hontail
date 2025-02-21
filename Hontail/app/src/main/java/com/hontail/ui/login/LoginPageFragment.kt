package com.hontail.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.hontail.R
import com.hontail.base.BaseFragment
import com.hontail.databinding.FragmentLoginPageBinding
import com.hontail.ui.MainActivityViewModel

class LoginPageFragment : BaseFragment<FragmentLoginPageBinding>(
    FragmentLoginPageBinding::bind,
    R.layout.fragment_login_page
) {
    private var imageRes: Int? = null
    private var textTop: String? = null
    private var textBottom: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            imageRes = it.getInt("imageRes")
            textTop = it.getString("textTop")
            textBottom = it.getString("textBottom")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textviewLoginPagerTextTop.text = textTop
        binding.textviewLoginPagerTextBottom.text = textBottom
        imageRes?.let { binding.imageViewLoginPagerCocktail.setImageResource(it) }
    }

    companion object {
        fun newInstance(imageRes: Int, textTop: String, textBottom: String) =
            LoginPageFragment().apply {
                arguments = Bundle().apply {
                    putInt("imageRes", imageRes)
                    putString("textTop", textTop)
                    putString("textBottom", textBottom)
                }
            }
    }
}