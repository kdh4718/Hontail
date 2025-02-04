package com.hontail.util

import android.app.AlertDialog
import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.hontail.R
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object CommonUtils {

    //천단위 콤마
    fun makeComma(num: Int): String {
        val comma = DecimalFormat("#,###")
        return "${comma.format(num)}"
    }

    fun changeTextColor(
        context: Context,
        fullText: String,
        changeText: String,
        color: Int
    ): SpannableString {
        val spannableString = SpannableString(fullText)
        val startIndex = fullText.indexOf(changeText)
        val endIndex = startIndex + changeText.length

        spannableString.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context, color)),
            startIndex,
            endIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        return spannableString
    }

    fun showDialog(
        context: Context,
        titleText: String,
        subtitleText: String,
        onConfirmClick: () -> Unit // ✅ 확인 버튼 클릭 시 실행할 콜백 함수 추가
    ) {
        // 다이얼로그 뷰 생성
        val dialogView: View = LayoutInflater.from(context)
            .inflate(R.layout.custom_dialog_need_ingredient, null)

        // 다이얼로그 빌더 설정
        val dialog = AlertDialog.Builder(context)
            .setView(dialogView) // 다이얼로그에 XML 레이아웃 적용
            .create()

        // 다이얼로그 표시
        dialog.show()

        // TextView에 전달받은 텍스트 적용
        dialogView.findViewById<TextView>(R.id.textViewCustomDialogIngredientTop).text = titleText
        dialogView.findViewById<TextView>(R.id.textViewCustomDialogIngredientBottom).text = subtitleText

        // 확인 버튼 클릭 시 동작 (콜백 실행)
        val confirmButton = dialogView.findViewById<MaterialButton>(R.id.buttonCustomDialogIngredientConfirm)
        confirmButton.setOnClickListener {
            onConfirmClick() // ✅ 호출한 곳에서 정의한 동작 실행
            dialog.dismiss() // 다이얼로그 종료
        }

        // 취소 버튼 클릭 시 동작
        val cancelButton = dialogView.findViewById<MaterialButton>(R.id.buttonCustomDialogIngredientCancel)
        cancelButton.setOnClickListener {
            dialog.dismiss() // 다이얼로그 종료
        }
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
        MY_PAGE_NICKNAME_MODIFY_FRAGMENT("MyPageNicknameModifyFragment"),
        COCKTAIL_PICTURE_RESULT_FRAGMENT("CocktailPictureResultFragment"),
        COCKTAIL_TAKE_PICTURE_FRAGMENT("CocktailTakePictureFragment"),
        PROFILE_FRAGMENT("ProfileFragment"),
        ZZIM_FRAGMENT("ZzimFragment"),
        FILTERBOTTOMSHEETFRAGMENT("FilterBottomSheetFragment")
    }

    enum class BartenderRecordMode {
        READY,
        RECORDING,
        COMPLETED
    }
}