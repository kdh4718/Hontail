package com.hontail.ui.cocktail

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.activityViewModels
import com.hontail.R
import com.hontail.base.BaseFragment
import com.hontail.databinding.DrawerCocktailRecipeBinding
import com.hontail.databinding.FragmentCocktailRecipeBinding
import com.hontail.databinding.FragmentZzimBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.MainActivityViewModel

class CocktailRecipeFragment : Fragment() {
    private var _binding: DrawerCocktailRecipeBinding? = null
    private val binding get() = _binding!!

    private var _contentBinding: FragmentCocktailRecipeBinding? = null
    private val contentBinding get() = _contentBinding!!

    private lateinit var mainActivity: MainActivity
    private val activityViewModel: MainActivityViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DrawerCocktailRecipeBinding.inflate(inflater, container, false)
        mainActivity.hideBottomNav(true)
        // ✅ include된 fragment_cocktail_recipe.xml의 바인딩 직접 가져오기
        _contentBinding = FragmentCocktailRecipeBinding.bind(binding.includeDrawerCocktailRecipeInclude.root)

        // 혹시 모르니 코드로도 투명 배경 적용(중복 설정일 수 있음)
        binding.drawerLayoutDrawerCocktailRecipeDrawer.setBackgroundColor(
            ContextCompat.getColor(requireContext(), android.R.color.transparent)
        )

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        // 가로 모드로 변경
        mainActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
    }

    private fun initEvent() {
        binding.apply {
            // ✅ Drawer 열고 닫는 이벤트 정상 작동하도록 설정
            includeDrawerCocktailRecipeInclude.imageButtonCocktailRecipeSideBar.setOnClickListener {
                drawerLayoutDrawerCocktailRecipeDrawer.openDrawer(GravityCompat.END)
            }

            imageViewDrawerCocktailRecipeClose.setOnClickListener {
                drawerLayoutDrawerCocktailRecipeDrawer.closeDrawers()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mainActivity.hideBottomNav(false)
        // 가로 모드로 변경
        mainActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        _binding = null
        _contentBinding = null
    }
}
