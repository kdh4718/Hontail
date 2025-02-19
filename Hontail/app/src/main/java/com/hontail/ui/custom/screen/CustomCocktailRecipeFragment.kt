package com.hontail.ui.custom.screen

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.hontail.R
import com.hontail.base.BaseFragment
import com.hontail.data.model.request.CustomCocktailRecipeRequest
import com.hontail.databinding.FragmentCustomCocktailRecipeBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.MainActivityViewModel
import com.hontail.ui.custom.adapter.CustomCocktailRecipeStepAdapter
//import com.hontail.ui.custom.adapter.CustomCocktailRecipeAdapter
import com.hontail.ui.custom.viewmodel.CustomCocktailRecipeViewModel
import com.hontail.util.CommonUtils
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException

private const val TAG = "CustomCocktailRecipeFra"
class CustomCocktailRecipeFragment: BaseFragment<FragmentCustomCocktailRecipeBinding>(
    FragmentCustomCocktailRecipeBinding::bind,
    R.layout.fragment_custom_cocktail_recipe
) {

    private lateinit var mainActivity: MainActivity

    private val activityViewModel: MainActivityViewModel by activityViewModels()
    private val viewModel: CustomCocktailRecipeViewModel by viewModels()

    private lateinit var customCocktailRecipeStepAdapter: CustomCocktailRecipeStepAdapter

    private lateinit var recipeMode: CommonUtils.CustomCocktailRecipeMode

    private lateinit var getImageLauncher: ActivityResultLauncher<String>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerImageLauncher()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeCustomCocktailRecipe()
        initToolbar()
        initAdapter()
        initEvent()
    }

    override fun onResume() {
        super.onResume()
        customCocktailRecipeStepAdapter.notifyDataSetChanged()
    }

    // 툴바 설정
    private fun initToolbar() {

        binding.apply {

            toolbarCustomCocktailRecipe.apply {

                setNavigationIcon(R.drawable.go_back)
                setNavigationOnClickListener {
                    parentFragmentManager.popBackStack(
                        "CustomCocktailRecipeFragment",
                        FragmentManager.POP_BACK_STACK_INCLUSIVE
                    )
                }
            }
        }
    }

    // ViewModel Observe 등록
    private fun observeCustomCocktailRecipe() {

        binding.apply {

            // 레시피 모드 등록 / 수정
            activityViewModel.recipeMode.observe(viewLifecycleOwner) { mode ->
                recipeMode = mode
                viewModel.initializeRecipeData(mode, activityViewModel.cocktailId.value, activityViewModel.userId)
            }

            // 칵테일 이미지
            viewModel.recipeImage.observe(viewLifecycleOwner) { imageUri ->

                if(imageUri != Uri.EMPTY) {
                    constraintLayoutListItemCustomCocktailRecipeImageGuide.visibility = View.GONE
                    imageViewListItemCustomCocktailRecipeImage.setImageURI(imageUri)
                }
                else {
                    imageViewListItemCustomCocktailRecipeImage.setImageResource(R.color.basic_gray)
                    constraintLayoutListItemCustomCocktailRecipeImageGuide.visibility = View.VISIBLE
                }
            }

            // 칵테일 이름 EditText와 ViewModel 연결
            editTextListItemCustomCocktailRecipeName.addTextChangedListener { text ->
                viewModel.updateRecipeName(text.toString())
            }
            
            // LiveData로 EditText 값 유지
            viewModel.recipeName.observe(viewLifecycleOwner) { name ->
                if(editTextListItemCustomCocktailRecipeName.text.toString() != name) {
                    editTextListItemCustomCocktailRecipeName.setText(name)
                }
            }

            // 칵테일 설명 EditText와 ViewModel 연결
            editTextListItemCustomCocktailRecipeDescription.addTextChangedListener { text ->
                viewModel.updateDescription(text.toString())
            }

            // LiveData로 EditText 값 유지
            viewModel.description.observe(viewLifecycleOwner) { description ->
                if(editTextListItemCustomCocktailRecipeDescription.text.toString() != description) {
                    editTextListItemCustomCocktailRecipeDescription.setText(description)
                }
            }

            // 도수
            activityViewModel.overallAlcoholContent.observe(viewLifecycleOwner) { alcoholContent ->
                textViewListItemCustomCocktailRecipeAlcoholLevelAlcoholLevel.text = "${alcoholContent.toInt()}도"
            }

            // 재료 리스트 새롭게 변환.
            activityViewModel.customCocktailIngredients.observe(viewLifecycleOwner) { ingredientList ->
                viewModel.setRecipeIngredients(ingredientList)
            }

            // 단계별 레시피들
            activityViewModel.recipeSteps.observe(viewLifecycleOwner) { newSteps ->
                customCocktailRecipeStepAdapter.items = newSteps.toMutableList()
                Log.d(TAG, "observeCustomCocktailRecipe: $newSteps")
                customCocktailRecipeStepAdapter.notifyDataSetChanged() // ✅ 전체 갱신 (혹은 notifyItemInserted 사용 가능)
            }

            if(activityViewModel.recipeMode.value == CommonUtils.CustomCocktailRecipeMode.MODIFY) {
                viewModel.alcoholLevel.observe(viewLifecycleOwner) { alcoholContent ->
                    activityViewModel.setOverAllAlcoholContent(alcoholContent)
                }
            }

            viewModel.uploadedImageUrl.observe(viewLifecycleOwner) { uploadedImageUrl ->

                constraintLayoutListItemCustomCocktailRecipeImageGuide.visibility = View.GONE
                Glide.with(mainActivity)
                    .load(uploadedImageUrl)
                    .into(imageViewListItemCustomCocktailRecipeImage)
            }
        }
    }

    // 리사이클러뷰 어댑터 연결
    private fun initAdapter() {

        binding.apply {

            customCocktailRecipeStepAdapter = CustomCocktailRecipeStepAdapter(mutableListOf())

            recyclerViewListItemCustomCocktailRecipeStep.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            recyclerViewListItemCustomCocktailRecipeStep.adapter = customCocktailRecipeStepAdapter
            recyclerViewListItemCustomCocktailRecipeStep.isNestedScrollingEnabled = false
        }
    }

    // 이벤트
    private fun initEvent() {

        binding.apply {

            // 이미지 추가.
            constraintLayoutListItemCustomCocktailRecipeImageGuide.setOnClickListener {
                getImageLauncher.launch("image/*")
            }

            // 이미지 수정
            imageViewListItemCustomCocktailRecipeImage.setOnClickListener {
                getImageLauncher.launch("image/*")
            }

            // 단계별 레시피 추가
            imageViewListItemCustomCocktailRecipeAddStep.setOnClickListener {
                val bottomSheet = CustomCocktailRecipeStepBottomSheetFragment()
                bottomSheet.show(parentFragmentManager, bottomSheet.tag)
            }

            customCocktailRecipeStepAdapter.customCocktailRecipeStepListener = object : CustomCocktailRecipeStepAdapter.ItemOnClickListener {

                // 레시피 단계 삭제
                override fun onClickDelete(position: Int) {
                    activityViewModel.deleteRecipeStep(position)
                }

            }
            
            // 등록
            buttonCustomCocktailRecipeRegister.setOnClickListener {

                Log.d(TAG, "initEvent: recipeMode : ${activityViewModel.recipeMode.value}")

                // 등록 모드일 때
                if(activityViewModel.recipeMode.value == CommonUtils.CustomCocktailRecipeMode.REGISTER) {

                    if(editTextListItemCustomCocktailRecipeName.text.isNullOrEmpty() || editTextListItemCustomCocktailRecipeDescription.text.isNullOrEmpty() || viewModel.uploadedImageUrl.value.isNullOrEmpty() || activityViewModel.recipeSteps.value.isNullOrEmpty()) {

                        Log.d(TAG, "initEvent: editTextListItemCustomCocktailRecipeName: ${editTextListItemCustomCocktailRecipeName.text}")
                        Log.d(TAG, "initEvent: editTextListItemCustomCocktailRecipeDescription: ${editTextListItemCustomCocktailRecipeDescription.text}")
                        Log.d(TAG, "initEvent: uploadedImageUrl: ${viewModel.uploadedImageUrl.value}")
                        Log.d(TAG, "initEvent: recipeSteps: ${viewModel.recipeSteps.value}")

                        Toast.makeText(mainActivity, "필수인 영역을 채워주세요.", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    // 이미지 url
                    val imageUrl = viewModel.uploadedImageUrl.value
                    Log.d(TAG, "initEvent: imageUrl: $imageUrl")

                    // 칵테일 이름
                    val name = viewModel.recipeName.value
                    Log.d(TAG, "initEvent: name: $name")

                    // 칵테일 도수
                    val alcoholContent = activityViewModel.overallAlcoholContent.value
                    Log.d(TAG, "initEvent: alcoholContent: $alcoholContent")

                    // 칵테일 설명
                    val description = viewModel.description.value
                    Log.d(TAG, "initEvent: description: $description")

                    // 칵테일 재료 리스트
                    val ingredientList = viewModel.recipeIngredients.value
                    if (ingredientList != null) {
                        for (ingredient in ingredientList) {
                            Log.d(TAG, "initEvent: ingredient: $ingredient")
                        }
                    }

                    // ingredientList에서 IngredientItem만 필터링
                    val baseSpirit = activityViewModel.customCocktailIngredients.value
                        ?.filterIsInstance<CustomCocktailItem.IngredientItem>() // IngredientItem만 필터링
                        ?.filter { it.ingredientCategoryKor == "베이스 주류" } // 베이스 주류만 필터링
                        ?.sortedByDescending { it.alcoholContent } // ingredientId 기준 오름차순 정렬
                        ?.firstOrNull() // 가장 첫 번째 값 선택
                        ?.ingredientType
                        ?.let { ingredientType ->
                            when(ingredientType) {
                                "Liqueur" -> "리큐어"
                                "Rum" -> "럼"
                                "Wine" -> "와인"
                                "Others", null -> "기타"
                                "Vodka" -> "보드카"
                                "Brandy" -> "브랜디"
                                "Whisky" -> "위스키"
                                "Tequila" -> "데킬라"
                                "Gin" -> "진"
                                else -> "기타"
                            }
                        } ?: "기타"
                    Log.d(TAG, "initEvent: baseSpirit: $baseSpirit")

                    // 커스텀인지
                    val isCustom = 1

                    // 만든 사람
                    val makerNickname = activityViewModel.userNickname // 여기 나중에 수정해야 함.

                    // 레시피 스텝
                    val recipeSteps = activityViewModel.recipeSteps.value
                    if (recipeSteps != null) {
                        for (recipe in recipeSteps) {
                            Log.d(TAG, "initEvent: recipe : $recipe")
                        }
                    }

                    val userId = activityViewModel.userId

                    val request = recipeSteps?.let { it1 ->
                        CustomCocktailRecipeRequest(
                            alcoholContent = alcoholContent!!.toInt(),
                            baseSpirit = baseSpirit.toString(),
                            cocktailName = name!!,
                            description = description!!,
                            imageUrl = imageUrl!!,
                            ingredients = ingredientList!!,
                            isCustom = isCustom,
                            makerNickname = makerNickname,
                            recipes = it1
                        )
                    }

                    viewModel.insertCustomCocktail(userId!!, request!!,
                        onSuccess = { cocktailId ->
                            Toast.makeText(requireContext(), "칵테일 등록 완료! $cocktailId", Toast.LENGTH_LONG).show()

                            activityViewModel.setCocktailId(cocktailId)

                            // 🔥 CustomCocktailRecipeFragment 제거
//                            parentFragmentManager.popBackStack("CustomCocktailRecipeFragment", 0)

                            // 🔥 CustomCocktailFragment 제거
                            parentFragmentManager.popBackStack("CustomCocktailFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)
//
//                            Log.d(TAG, "🎯 changeFragment 호출 전: $cocktailId")
//                            mainActivity.changeFragment(CommonUtils.MainFragmentName.COCKTAIL_DETAIL_FRAGMENT)
//                            Log.d(TAG, "🎯 changeFragment 호출 후")

                            activityViewModel.clearCustomCocktailIngredient()
                            activityViewModel.clearRecipeStep()
                        },
                        onError = { errorMessage ->
                            Toast.makeText(requireContext(), "오류 발생: $errorMessage", Toast.LENGTH_LONG).show()
                            Log.d(TAG, "initEvent: $errorMessage")
                        }
                    )
                }

                if(activityViewModel.recipeMode.value == CommonUtils.CustomCocktailRecipeMode.MODIFY) {

                    if(editTextListItemCustomCocktailRecipeName.text.isNullOrEmpty() || editTextListItemCustomCocktailRecipeDescription.text.isNullOrEmpty() || viewModel.uploadedImageUrl.value.isNullOrEmpty() || activityViewModel.recipeSteps.value.isNullOrEmpty()) {

                        Toast.makeText(mainActivity, "필수인 영역을 채워주세요.", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    // 이미지 url
                    val imageUrl = viewModel.uploadedImageUrl.value
                    Log.d(TAG, "initEvent: imageUrl: $imageUrl")

                    // 칵테일 이름
                    val name = viewModel.recipeName.value
                    Log.d(TAG, "initEvent: name: $name")

                    // 칵테일 도수
                    val alcoholContent = activityViewModel.overallAlcoholContent.value
                    Log.d(TAG, "initEvent: alcoholContent: $alcoholContent")

                    // 칵테일 설명
                    val description = viewModel.description.value
                    Log.d(TAG, "initEvent: description: $description")

                    // 칵테일 재료 리스트
                    val ingredientList = viewModel.recipeIngredients.value
                    if (ingredientList != null) {
                        for (ingredient in ingredientList) {
                            Log.d(TAG, "initEvent: ingredient: $ingredient")
                        }
                    }

                    // ingredientList에서 IngredientItem만 필터링
                    val baseSpirit = activityViewModel.customCocktailIngredients.value
                        ?.filterIsInstance<CustomCocktailItem.IngredientItem>() // IngredientItem만 필터링
                        ?.filter { it.ingredientCategoryKor == "베이스 주류" } // 베이스 주류만 필터링
                        ?.sortedByDescending { it.alcoholContent } // ingredientId 기준 오름차순 정렬
                        ?.firstOrNull() // 가장 첫 번째 값 선택
                        ?.ingredientType
                        ?.let { ingredientType ->
                            when(ingredientType) {
                                "Liqueur" -> "리큐어"
                                "Rum" -> "럼"
                                "Wine" -> "와인"
                                "Others", null -> "기타"
                                "Vodka" -> "보드카"
                                "Brandy" -> "브랜디"
                                "Whisky" -> "위스키"
                                "Tequila" -> "데킬라"
                                "Gin" -> "진"
                                else -> "기타"
                            }
                        } ?: "기타"
                    Log.d(TAG, "initEvent: baseSpirit: $baseSpirit")

                    // 커스텀인지
                    val isCustom = 1

                    // 만든 사람
                    val makerNickname = activityViewModel.userNickname // 여기 나중에 수정해야 함.

                    // 레시피 스텝
                    val recipeSteps = activityViewModel.recipeSteps.value
                    if (recipeSteps != null) {
                        for (recipe in recipeSteps) {
                            Log.d(TAG, "initEvent: recipe : $recipe")
                        }
                    }

                    val userId = activityViewModel.userId

                    val request = recipeSteps?.let { it1 ->
                        CustomCocktailRecipeRequest(
                            alcoholContent = alcoholContent!!.toInt(),
                            baseSpirit = baseSpirit.toString(),
                            cocktailName = name!!,
                            description = description!!,
                            imageUrl = imageUrl!!,
                            ingredients = ingredientList!!,
                            isCustom = isCustom,
                            makerNickname = makerNickname,
                            recipes = it1
                        )
                    }

                    viewModel.updateCustomCocktail(activityViewModel.cocktailId.value!!, request!!,
                        onSuccess = { cocktailId ->
                            Toast.makeText(requireContext(), "칵테일 수정 완료! $cocktailId", Toast.LENGTH_LONG).show()

                            activityViewModel.setCocktailId(cocktailId)

                            parentFragmentManager.popBackStack("CustomCocktailRecipeFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)

                            activityViewModel.clearCustomCocktailIngredient()
                            activityViewModel.clearRecipeStep()
                        },
                        onError = { errorMessage ->
                            Toast.makeText(requireContext(), "오류 발생: $errorMessage", Toast.LENGTH_LONG).show()
                            Log.d(TAG, "initEvent: $errorMessage")
                        }
                    )
                }
            }
        }
    }

    // 이미지 런처 등록
    private fun registerImageLauncher() {

        getImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->

            if(uri != null) {

                viewModel.updateRecipeImage(uri)

                val imageFileName = getImageFileName(uri)

                lifecycleScope.launch {
                    val presignedUrl = viewModel.uploadImageToServer(imageFileName)
                    Log.d(TAG, "registerImageLauncher: $presignedUrl")

                    if (presignedUrl.isNotBlank()) {
                        // presignedUrl이 정상적으로 받아졌다면 S3로 업로드를 진행합니다.
                        uploadImageToS3(presignedUrl, uri, requireContext(),
                            onSuccess = {
                                Log.d(TAG, "registerImageLauncher: 이미지 업로드 성공")
                                viewModel.setUploadedImageUrl(presignedUrl)
                            },
                            onFailure = { error ->
                                Log.d(TAG, "registerImageLauncher: 이미지 업로드 실패")
                            }
                        )
                    } else {
                        // 오류 처리: presignedUrl이 비어 있다면, 서버 호출에 실패한 것임.
                        Log.d(TAG, "registerImageLauncher: 서버 통신 오류: presigned URL을 받아오지 못했습니다.")
                    }
                }
            }
        }
    }

    // 이미지 파일 이름 가져오기
    @SuppressLint("Range")
    private fun getImageFileName(uri: Uri): String {

        var result: String? = null
        if (uri.scheme == "content") {
            val cursor: Cursor? = mainActivity.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    result = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/') ?: -1
            if (cut != -1) {
                result = result?.substring(cut + 1)
            }
        }
        return result ?: "default_filename"
    }

    private fun readBytesFromUri(context: Context, uri: Uri): ByteArray? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.readBytes()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun uploadImageToS3(
        presignedUrl: String,
        imageUri: Uri,
        context: Context,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        // 1. 이미지 데이터를 바이트 배열로 읽음
        val imageBytes = readBytesFromUri(context, imageUri)
        if (imageBytes == null) {
            onFailure(Exception("이미지 데이터를 읽어오지 못했습니다."))
            return
        }

        // 2. ContentResolver로 MIME 타입을 가져옴
        val mimeType = context.contentResolver.getType(imageUri) ?: "image/jpeg"

        // 3. OkHttp를 사용하여 PUT 요청 생성
        val client = OkHttpClient()
        val mediaType = mimeType.toMediaType()
        val requestBody = imageBytes.toRequestBody(mediaType)

        val request = Request.Builder()
            .url(presignedUrl)
            .put(requestBody)
            .addHeader("Content-Type", mimeType)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // 업로드 실패 처리
                onFailure(e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    // 업로드 성공
                    onSuccess()
                } else {
                    onFailure(Exception("업로드 실패: ${response.code} ${response.message}"))
                }
                response.close()
            }
        })
    }

}