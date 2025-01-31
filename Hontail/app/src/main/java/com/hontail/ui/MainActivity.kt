package com.hontail.ui

import android.Manifest
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hontail.R
import com.hontail.base.BaseActivity
import com.hontail.databinding.ActivityMainBinding
import com.hontail.ui.alarm.AlarmFragment
import com.hontail.ui.bartender.BatenderFragment
import com.hontail.ui.cocktail.CocktailDetailFragment
import com.hontail.ui.cocktail.CocktailListFragment
import com.hontail.ui.cocktail.CocktailRecipeFragment
import com.hontail.ui.cocktail.CocktailSearchFragment
import com.hontail.ui.custom.CustomCocktailFragment
import com.hontail.ui.custom.CustomCocktailModifyFragment
import com.hontail.ui.home.HomeFragment
import com.hontail.ui.ingredient.IngredientAddFragment
import com.hontail.ui.ingredient.IngredientListFragment
import com.hontail.ui.mypage.MyPageFragment
import com.hontail.ui.mypage.MyPageModifyFragment
import com.hontail.ui.picture.CocktailPictureResultFragment
import com.hontail.ui.picture.CocktailTakePictureFragment
import com.hontail.ui.picture.FilterBottomSheetFragment
import com.hontail.ui.profile.ProfileFragment
import com.hontail.ui.zzim.ZzimFragment
import com.hontail.util.CommonUtils
import com.hontail.util.PermissionChecker

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    /** permission check **/
    private val checker = PermissionChecker(this)
    private val runtimePermissions = arrayOf(Manifest.permission.CAMERA)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.TRANSPARENT
//        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        checkPermissions()
        changeFragment(CommonUtils.MainFragmentName.HOME_FRAGMENT)
    }

    fun checkPermissions() {
        /* permission check */
        if (!checker.checkPermission(this, runtimePermissions)) {
            checker.requestPermissionLauncher.launch(runtimePermissions)
        }
    }

    fun changeFragment(name: CommonUtils.MainFragmentName) {
        val transaction = supportFragmentManager.beginTransaction()

        when (name) {
            CommonUtils.MainFragmentName.HOME_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, HomeFragment())
            }

            CommonUtils.MainFragmentName.ALARM_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, AlarmFragment())
            }

            CommonUtils.MainFragmentName.BARTENDER_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, BatenderFragment())
                    .addToBackStack("bartenderFragment")
            }

            CommonUtils.MainFragmentName.COCKTAIL_DETAIL_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, CocktailDetailFragment())
            }

            CommonUtils.MainFragmentName.COCKTAIL_LIST_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, CocktailListFragment())
            }

            CommonUtils.MainFragmentName.COCKTAIL_RECIPE_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, CocktailRecipeFragment())
            }

            CommonUtils.MainFragmentName.COCKTAIL_SEARCH_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, CocktailSearchFragment())
            }

            CommonUtils.MainFragmentName.CUSTOM_COCKTAIL_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, CustomCocktailFragment())
            }

            CommonUtils.MainFragmentName.CUSTOM_COCKTAIL_MODIFY_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, CustomCocktailModifyFragment())
            }

            CommonUtils.MainFragmentName.INGREDIENT_ADD_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, IngredientAddFragment())
            }

            CommonUtils.MainFragmentName.INGREDIENT_LIST_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, IngredientListFragment())
            }

            CommonUtils.MainFragmentName.MY_PAGE_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, MyPageFragment())
            }

            CommonUtils.MainFragmentName.MY_PAGE_MODIFY_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, MyPageModifyFragment())
            }

            CommonUtils.MainFragmentName.COCKTAIL_PICTURE_RESULT_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, CocktailPictureResultFragment())
            }

            CommonUtils.MainFragmentName.COCKTAIL_TAKE_PICTURE_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, CocktailTakePictureFragment())
                    .addToBackStack(null)
            }

            CommonUtils.MainFragmentName.PROFILE_FRAGMENT -> {
                transaction.replace(R.id.frameLayoutMainFragment, ProfileFragment())
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


    fun hideBottomNav(state : Boolean){
        if(state) binding.bottomNavigation.visibility = View.GONE
        else binding.bottomNavigation.visibility = View.VISIBLE
    }

}