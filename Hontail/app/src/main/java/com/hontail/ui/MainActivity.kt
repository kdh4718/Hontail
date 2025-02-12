package com.hontail.ui

import android.Manifest
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.hontail.R
import com.hontail.base.BaseActivity
import com.hontail.databinding.ActivityMainBinding
import com.hontail.ui.alarm.AlarmFragment
import com.hontail.ui.bartender.BatenderFragment
import com.hontail.ui.custom.screen.CustomCocktailBottomSheetFragment
import com.hontail.ui.custom.screen.CustomCocktailFragment
import com.hontail.ui.custom.screen.CustomCocktailIngredientDetailFragment
import com.hontail.ui.custom.screen.CustomCocktailRecipeFragment
import com.hontail.ui.custom.screen.CustomCocktailSearchFragment
import com.hontail.ui.cocktail.screen.CocktailDetailFragment
import com.hontail.ui.cocktail.screen.CocktailListFragment
import com.hontail.ui.cocktail.screen.CocktailRecipeFragment
import com.hontail.ui.cocktail.screen.CocktailSearchFragment
import com.hontail.ui.home.screen.HomeFragment
import com.hontail.ui.ingredient.IngredientAddFragment
import com.hontail.ui.ingredient.IngredientListFragment
import com.hontail.ui.mypage.MyPageFragment
import com.hontail.ui.mypage.MyPageModifyFragment
import com.hontail.ui.mypage.MyPageNicknameModifyFragment
import com.hontail.ui.picture.CocktailPictureResultFragment
import com.hontail.ui.picture.CocktailTakePictureFragment
import com.hontail.ui.cocktail.screen.FilterBottomSheetFragment
import com.hontail.ui.profile.ProfileFragment
import com.hontail.ui.zzim.ZzimFragment
import com.hontail.util.CommonUtils
import com.hontail.util.PermissionChecker

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    private val activityViewModel: MainActivityViewModel by viewModels()

    /** permission check **/
    private val checker = PermissionChecker(this)
    private val runtimePermissions = arrayOf(Manifest.permission.CAMERA)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.TRANSPARENT

        checkPermissions()
        initData()        
        initBottomNavigation()
        initBackStackListener()
        changeFragment(CommonUtils.MainFragmentName.HOME_FRAGMENT)
    }

    private fun initBackStackListener() {
        supportFragmentManager.addOnBackStackChangedListener {
            // 현재 표시되는 Fragment 확인
            val currentFragment = supportFragmentManager.findFragmentById(R.id.frameLayoutMainFragment)

            // Fragment 타입에 따라 bottom navigation 아이템 선택
            when (currentFragment) {
                is HomeFragment -> setSelectedBottomNavigation(R.id.navigation_home)
                is CocktailListFragment -> setSelectedBottomNavigation(R.id.navigation_search)
                is ZzimFragment -> setSelectedBottomNavigation(R.id.navigation_heart)
                is MyPageFragment -> setSelectedBottomNavigation(R.id.navigation_mypage)
            }
        }
    }

    private fun initBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    changeFragment(CommonUtils.MainFragmentName.HOME_FRAGMENT)
                    true
                }
                R.id.navigation_search -> {
                    changeFragment(CommonUtils.MainFragmentName.COCKTAIL_LIST_FRAGMENT)
                    true
                }
                R.id.navigation_plus -> {
                    changeFragment(CommonUtils.MainFragmentName.CUSTOM_COCKTAIL_FRAGMENT)
                    true
                }
                R.id.navigation_heart -> {
                    changeFragment(CommonUtils.MainFragmentName.ZZIM_FRAGMENT)
                    true
                }
                R.id.navigation_mypage -> {
                    changeFragment(CommonUtils.MainFragmentName.MY_PAGE_FRAGMENT)
                    true
                }
                else -> false
            }
        }
    }

    fun setSelectedBottomNavigation(itemId: Int) {
        binding.bottomNavigation.selectedItemId = itemId
    }

    fun initData(){
        val userId = intent.getIntExtra("user_id", 0)
        activityViewModel.userId = userId

        val userNickname = intent.getStringExtra("user_nickname")
        activityViewModel.userNickname = userNickname ?: ""
    }

    fun checkPermissions() {
        /* permission check */
        if (!checker.checkPermission(this, runtimePermissions)) {
            checker.requestPermissionLauncher.launch(runtimePermissions)
        }
    }

    fun changeFragmentWithCallback(
        fragmentName: CommonUtils.MainFragmentName,
        callback: () -> Unit
    ) {
        val transaction = supportFragmentManager.beginTransaction()

        when (fragmentName) {
            CommonUtils.MainFragmentName.HOME_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, HomeFragment())
            }
            CommonUtils.MainFragmentName.ALARM_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, AlarmFragment())
                    .addToBackStack("AlarmFragment")
            }
            CommonUtils.MainFragmentName.BARTENDER_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, BatenderFragment())
                    .addToBackStack("bartenderFragment")
            }
            CommonUtils.MainFragmentName.COCKTAIL_DETAIL_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, CocktailDetailFragment())
                    .addToBackStack("CocktailDetailFragment")
            }
            CommonUtils.MainFragmentName.COCKTAIL_LIST_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, CocktailListFragment())
            }
            CommonUtils.MainFragmentName.COCKTAIL_RECIPE_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, CocktailRecipeFragment())
                    .addToBackStack("CocktailRecipeFragment")
            }
            CommonUtils.MainFragmentName.COCKTAIL_SEARCH_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, CocktailSearchFragment())
                    .addToBackStack("CocktailSearchFragment")
            }
            CommonUtils.MainFragmentName.CUSTOM_COCKTAIL_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, CustomCocktailFragment())
                    .addToBackStack("CustomCocktailFragment")
            }
            CommonUtils.MainFragmentName.CUSTOM_COCKTAIL_SEARCH_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, CustomCocktailSearchFragment())
                    .addToBackStack("CustomCocktailSearchFragment")
            }
            CommonUtils.MainFragmentName.CUSTOM_COCKTAIL_INGREDIENT_DETAIL_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, CustomCocktailIngredientDetailFragment())
                    .addToBackStack("CustomCocktailIngredientDetailFragment")
            }
            CommonUtils.MainFragmentName.CUSTOM_COCKTAIL_BOTTOM_SHEET_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, CustomCocktailBottomSheetFragment())
            }
            CommonUtils.MainFragmentName.CUSTOM_COCKTAIL_RECIPE_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, CustomCocktailRecipeFragment())
                    .addToBackStack("CustomCocktailRecipeFragment")
            }
            CommonUtils.MainFragmentName.INGREDIENT_ADD_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, IngredientAddFragment())
                    .addToBackStack("IngredientAddFragment")
            }
            CommonUtils.MainFragmentName.INGREDIENT_LIST_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, IngredientListFragment())
            }
            CommonUtils.MainFragmentName.MY_PAGE_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, MyPageFragment())
            }
            CommonUtils.MainFragmentName.MY_PAGE_MODIFY_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, MyPageModifyFragment())
                    .addToBackStack("MyPageModifyFragment")
            }
            CommonUtils.MainFragmentName.MY_PAGE_NICKNAME_MODIFY_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, MyPageNicknameModifyFragment())
                    .addToBackStack("MyPageNicknameModifyFragment")
            }
            CommonUtils.MainFragmentName.COCKTAIL_PICTURE_RESULT_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, CocktailPictureResultFragment())
                    .addToBackStack("CocktailPictureResultFragment")
            }
            CommonUtils.MainFragmentName.COCKTAIL_TAKE_PICTURE_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, CocktailTakePictureFragment())
                    .addToBackStack(null)
            }
            CommonUtils.MainFragmentName.PROFILE_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, ProfileFragment())
                    .addToBackStack("ProfileFragment")
            }
            CommonUtils.MainFragmentName.ZZIM_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, ZzimFragment())
            }
            CommonUtils.MainFragmentName.FILTERBOTTOMSHEETFRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, FilterBottomSheetFragment())
            }
        }

        transaction.commit()
        supportFragmentManager.executePendingTransactions()
        callback.invoke()
    }

    fun changeFragment(name: CommonUtils.MainFragmentName) {
        val transaction = supportFragmentManager.beginTransaction()

        when (name) {
            CommonUtils.MainFragmentName.HOME_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, HomeFragment())
            }
            CommonUtils.MainFragmentName.ALARM_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, AlarmFragment())
                    .addToBackStack("AlarmFragment")
            }
            CommonUtils.MainFragmentName.BARTENDER_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, BatenderFragment())
                    .addToBackStack("bartenderFragment")
            }
            CommonUtils.MainFragmentName.COCKTAIL_DETAIL_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, CocktailDetailFragment())
                    .addToBackStack("CocktailDetailFragment")
            }
            CommonUtils.MainFragmentName.COCKTAIL_LIST_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, CocktailListFragment())
            }
            CommonUtils.MainFragmentName.COCKTAIL_RECIPE_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, CocktailRecipeFragment())
                    .addToBackStack("CocktailRecipeFragment")
            }
            CommonUtils.MainFragmentName.COCKTAIL_SEARCH_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, CocktailSearchFragment())
                    .addToBackStack("CocktailSearchFragment")
            }
            CommonUtils.MainFragmentName.CUSTOM_COCKTAIL_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, CustomCocktailFragment())
                    .addToBackStack("CustomCocktailFragment")
            }
            CommonUtils.MainFragmentName.CUSTOM_COCKTAIL_SEARCH_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, CustomCocktailSearchFragment())
                    .addToBackStack("CustomCocktailSearchFragment")
            }
            CommonUtils.MainFragmentName.CUSTOM_COCKTAIL_INGREDIENT_DETAIL_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, CustomCocktailIngredientDetailFragment())
                    .addToBackStack("CustomCocktailIngredientDetailFragment")
            }
            CommonUtils.MainFragmentName.CUSTOM_COCKTAIL_BOTTOM_SHEET_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, CustomCocktailBottomSheetFragment())
            }
            CommonUtils.MainFragmentName.CUSTOM_COCKTAIL_RECIPE_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, CustomCocktailRecipeFragment())
                    .addToBackStack("CustomCocktailRecipeFragment")
            }
            CommonUtils.MainFragmentName.INGREDIENT_ADD_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, IngredientAddFragment())
                    .addToBackStack("IngredientAddFragment")
            }
            CommonUtils.MainFragmentName.INGREDIENT_LIST_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, IngredientListFragment())
            }
            CommonUtils.MainFragmentName.MY_PAGE_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, MyPageFragment())
            }
            CommonUtils.MainFragmentName.MY_PAGE_MODIFY_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, MyPageModifyFragment())
                    .addToBackStack("MyPageModifyFragment")
            }
            CommonUtils.MainFragmentName.MY_PAGE_NICKNAME_MODIFY_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, MyPageNicknameModifyFragment())
                    .addToBackStack("MyPageNicknameModifyFragment")
            }
            CommonUtils.MainFragmentName.COCKTAIL_PICTURE_RESULT_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, CocktailPictureResultFragment())
                    .addToBackStack("CocktailPictureResultFragment")
            }
            CommonUtils.MainFragmentName.COCKTAIL_TAKE_PICTURE_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, CocktailTakePictureFragment())
                    .addToBackStack(null)
            }
            CommonUtils.MainFragmentName.PROFILE_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, ProfileFragment())
                    .addToBackStack("ProfileFragment")
            }
            CommonUtils.MainFragmentName.ZZIM_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, ZzimFragment())
            }
            CommonUtils.MainFragmentName.FILTERBOTTOMSHEETFRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, FilterBottomSheetFragment())
            }
        }

        transaction.commit()
    }

    fun hideBottomNav(state: Boolean) {
        if (state) binding.bottomNavigation.visibility = View.GONE
        else binding.bottomNavigation.visibility = View.VISIBLE
    }
}