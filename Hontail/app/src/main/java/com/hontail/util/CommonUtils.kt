package com.hontail.util

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object CommonUtils {

    //천단위 콤마
    fun makeComma(num: Int): String {
        val comma = DecimalFormat("#,###")
        return "${comma.format(num)} ₩"
    }

    //날짜 포맷 출력
    fun dateformatYMDHM(time:Date):String{
        val format = SimpleDateFormat("yyyy.MM.dd. HH:mm", Locale.KOREA)
        format.timeZone = TimeZone.getTimeZone("Asia/Seoul")
        return format.format(time)
    }

    fun dateformatYMD(time: Date):String{
        val format = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA)
        format.timeZone = TimeZone.getTimeZone("Asia/Seoul")
        return format.format(time)
    }

    fun formatLongToDate(longDate: Long): String {
        val format = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())  // 원하는 날짜 형식 지정
        return format.format(Date(longDate))  // Long 값을 Date 객체로 변환 후 포맷 적용
    }

    enum class MainFragmentName(var str: String) {
        HOME_FRAGMENT("HomeFragment"),
        ALARM_FRAGMENT("AlarmFragment"),
        BARTENDER_FRAGMENT("BatenderFragment"),
        COCKTAIL_DETAIL_FRAGMENT("CocktailDetailFragment"),
        COCKTAIL_LIST_FRAGMENT("CocktailListFragment"),
        COCKTAIL_RECIPE_FRAGMENT("CocktailRecipeFragment"),
        COCKTAIL_SEARCH_FRAGMENT("CocktailSearchFragment"),
        CUSTOM_COCKTAIL_FRAGMENT("CustomCocktailFragment"),
        CUSTOM_COCKTAIL_MODIFY_FRAGMENT("CustomCocktailModifyFragment"),
        INGREDIENT_ADD_FRAGMENT("IngredientAddFragment"),
        INGREDIENT_LIST_FRAGMENT("IngredientListFragment"),
        MY_PAGE_FRAGMENT("MyPageFragment"),
        MY_PAGE_MODIFY_FRAGMENT("MyPageModifyFragment"),
        COCKTAIL_PICTURE_RESULT_FRAGMENT("CocktailPictureResultFragment"),
        COCKTAIL_TAKE_PICTURE_FRAGMENT("CocktailTakePictureFragment"),
        PROFILE_FRAGMENT("ProfileFragment"),
        ZZIM_FRAGMENT("ZzimFragment")
    }
}