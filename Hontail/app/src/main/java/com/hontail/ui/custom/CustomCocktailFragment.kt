package com.hontail.ui.custom

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.hontail.R
import com.hontail.base.BaseFragment
import com.hontail.data.local.IngredientRepository
import com.hontail.databinding.FragmentCustomCocktailBinding
import com.hontail.databinding.FragmentLoginBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.MainActivityViewModel
import com.hontail.util.CommonUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "CustomCocktailFragment"
class CustomCocktailFragment : BaseFragment<FragmentCustomCocktailBinding>(
    FragmentCustomCocktailBinding::bind,
    R.layout.fragment_custom_cocktail
) {
    private lateinit var mainActivity: MainActivity
    private val activityViewModel: MainActivityViewModel by activityViewModels()

    private lateinit var customCocktailAdapter: CustomCocktailAdapter

    private lateinit var ingredientRepository: IngredientRepository

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity

        ingredientRepository = IngredientRepository.getInstance()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainActivity.hideBottomNav(true)
        initToolbar()
        initAdapter()
        initEvent()
        testRoom()
    }

    // 툴바 설정
    private fun initToolbar() {

        binding.apply {

            toolbarCustomCocktail.apply {

                setNavigationIcon(R.drawable.go_back)
                setNavigationOnClickListener {
                    parentFragmentManager.popBackStack("CustomCocktailFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                }
            }
        }
    }

    // 리사이클러뷰 어댑터 연결
    private fun initAdapter() {

        binding.apply {

            val items = mutableListOf<CustomCocktailItem>().apply {
                add(CustomCocktailItem.IngredientItem("오렌지", "1/2 개"))
                add(CustomCocktailItem.IngredientItem("에스프레소", "20 ml"))
            }

            val items2 = mutableListOf<CustomCocktailItem>().apply {
                if(isIngredientListEmpty()) {
                    add(CustomCocktailItem.EmptyItem)
                }
            }

            customCocktailAdapter = CustomCocktailAdapter(items)

            recyclerViewCustomCocktail.layoutManager = LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false)
            recyclerViewCustomCocktail.adapter = customCocktailAdapter
        }
    }

    // 재료가 있는지 확인
    private fun isIngredientListEmpty(): Boolean {
        return true
    }

    // 이벤트
    private fun initEvent() {

        binding.apply {

            // 재료 삭제
            customCocktailAdapter.customCocktailIngredientDeleteListener = object : CustomCocktailAdapter.ItemOnClickListener {
                override fun onClick() {
                    TODO("Not yet implemented")
                }
            }

            // 재료 추가
            fabCustomCocktailIngredientAdd.setOnClickListener {
                mainActivity.changeFragment(CommonUtils.MainFragmentName.CUSTOM_COCKTAIL_SEARCH_FRAGMENT)
            }

            // 다음으로 넘어가기
            buttonCustomCocktailNext.setOnClickListener {
                mainActivity.changeFragment(CommonUtils.MainFragmentName.CUSTOM_COCKTAIL_RECIPE_FRAGMENT)
            }
        }
    }

    // Room DB 테스트 코드
    private fun testRoom() {

        CoroutineScope(Dispatchers.Main).launch {

            val ingredientList = ingredientRepository.getIngredients()

            Log.d(TAG, "testRoom: ${ingredientList.size}")
            
            for(ingredient in ingredientList) {
                Log.d(TAG, "ingredientName: ${ingredient.ingredientNameKor}")
            }
        }
    }

}

sealed class CustomCocktailItem {

    data class IngredientItem(val ingredientName: String, val ingredientQuantity: String): CustomCocktailItem()
    object EmptyItem: CustomCocktailItem()
}