package com.hontail.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hontail.data.model.request.BartenderRequest
import com.hontail.data.model.request.Recipe
import com.hontail.data.model.response.BartenderResponse
import com.hontail.data.model.response.Cocktail
import com.hontail.data.remote.RetrofitUtil
import com.hontail.ui.bartender.screen.ChatMessage
import com.hontail.ui.custom.screen.CustomCocktailItem
import com.hontail.util.CommonUtils
import kotlinx.coroutines.launch
import retrofit2.Response

private const val TAG = "MainActivityViewModel_SSAFY"

class MainActivityViewModel(private val handle: SavedStateHandle) : ViewModel() {
    // 사용자 아이디
    var userId = handle.get<Int>("userId") ?: 0
        set(value) {
            handle.set("userId", value)
            field = value
        }

    var userNickname = handle.get<String>("userNickname") ?: ""
        set(value) {
            handle.set("userNickname", value)
            field = value
        }

    // 선택된 cocktailId
    private val _cocktailId = MutableLiveData<Int>()
    val cocktailId: LiveData<Int>
        get() = _cocktailId

    // Vision API로 분석한 텍스트 저장
    private val _ingredientList = MutableLiveData<List<String>>()
    val ingredientList: LiveData<List<String>>
        get() = _ingredientList

    fun setCocktailId(cocktailId: Int) {
        _cocktailId.postValue(cocktailId)
    }

    fun setIngredientList(ingredientList: List<String>) {
        _ingredientList.postValue(ingredientList)
    }

    // 선택된 ingredientId
    private val _ingredientId = MutableLiveData<Int>()
    val ingredientId: LiveData<Int> get() = _ingredientId

    fun setIngredientId(ingredientId: Int) {
        _ingredientId.postValue(ingredientId)
    }

    // 커스텀 칵테일에 추가할 재료 목록
    private val _customCocktailIngredients = MutableLiveData<MutableList<CustomCocktailItem>>(
        mutableListOf()
    )
    val customCocktailIngredients: LiveData<MutableList<CustomCocktailItem>>
        get() = _customCocktailIngredients

    // 재료 추가
    fun addCustomCocktailIngredient(item: CustomCocktailItem) {
        val list = _customCocktailIngredients.value ?: mutableListOf()
        list.add(item)
        _customCocktailIngredients.value = list  // observer에 갱신 알림
    }

    // 재료 삭제
    fun deleteCustomCocktailIngredientAt(position: Int) {
        val list = _customCocktailIngredients.value ?: mutableListOf()
        if (position in list.indices) {
            list.removeAt(position)
            _customCocktailIngredients.value = list  // LiveData 업데이트로 Observer에 알림
        }
    }

    // 재료 리스트 초기화.
    fun clearCustomCocktailIngredient() {
        _customCocktailIngredients.value = mutableListOf()
    }

    // 레시피 단계 리스트
    private val _recipeSteps = MutableLiveData<MutableList<Recipe>>(mutableListOf())
    val recipeSteps: LiveData<MutableList<Recipe>> get() = _recipeSteps

    /**
     * 새로운 레시피 단계를 추가.
     * 최대 15단계까지 추가.
     */
    fun addNewRecipeStep(action: String?, guide: String) {
        _recipeSteps.value?.let { steps ->
            val newStepNumber = steps.size + 1
            val updatedSteps = steps.toMutableList()
            updatedSteps.add(Recipe(action, guide, newStepNumber))
            _recipeSteps.postValue(updatedSteps) // ✅ UI 갱신을 위해 postValue 사용
            Log.d("DEBUG", "🆕 레시피 단계 추가됨 (ViewModel): $updatedSteps") // ✅ 값 확인
        }
    }


    /**
     * 특정 위치의 레시피 단계를 삭제.
     * 삭제 후에는 단계 번호를 재정렬.
     */
    fun deleteRecipeStep(position: Int) {
        _recipeSteps.value?.let { steps ->
            if (position in steps.indices) {
                steps.removeAt(position)
                // 단계 번호 재정렬
                steps.forEachIndexed { index, step ->
                    step.sequence = index + 1
                }
                _recipeSteps.value = steps
            }
        }
    }

    // 특정 단계 레시피 업데이트
    fun updateRecipeStep(position: Int, newAction: String, newGuide: String) {
        _recipeSteps.value?.let { steps ->
            if (position in steps.indices) {
                val updatedList = steps.toMutableList()
                updatedList[position] = Recipe(newAction, newGuide, updatedList[position].sequence)
                _recipeSteps.postValue(updatedList)
            }
        }
    }

    // 단계 리스트 초기화
    fun clearRecipeStep() {
        _recipeSteps.value = mutableListOf()
    }

    // -------------------------------------------
    // 도수(알코올 농도) 계산 (가중 평균 방식)
    // 전체 도수 = (Σ (각 재료의 alcoholContent × 환산된 수량(ml))) / (Σ 환산된 수량(ml))
    // -------------------------------------------
    private val _overallAlcoholContent = MediatorLiveData<Double>()
    val overallAlcoholContent: LiveData<Double> get() = _overallAlcoholContent

    fun setOverAllAlcoholContent(alcoholContent: Int) {
        _overallAlcoholContent.value = alcoholContent.toDouble()
    }

    init {
        // _customCocktailIngredients가 변경될 때마다 전체 도수를 다시 계산
        _overallAlcoholContent.addSource(_customCocktailIngredients) { ingredientList ->
            _overallAlcoholContent.value = computeOverallAlcoholContent(ingredientList)
        }
    }

    private fun computeOverallAlcoholContent(ingredientList: List<CustomCocktailItem>): Double {
        // IngredientItem만 필터링
        val filteredList = ingredientList.filterIsInstance<CustomCocktailItem.IngredientItem>()
        var totalAlcoholVolume = 0.0
        var totalQuantityInMl = 0.0
        filteredList.forEach { ingredient ->
            // ingredient.ingredientQuantity는 "0.5 slices", "2.5 ml", "1 shot", "20 ml" 등으로 들어옵니다.
            val quantityInMl = parseIngredientQuantity(ingredient.ingredientQuantity) ?: 0.0
            totalAlcoholVolume += ingredient.alcoholContent * quantityInMl
            Log.d(
                TAG,
                "computeOverallAlcoholContent: ${ingredient.ingredientName} ${ingredient.alcoholContent}"
            )
            totalQuantityInMl += quantityInMl
        }
        return if (totalQuantityInMl == 0.0) 0.0 else totalAlcoholVolume / totalQuantityInMl
    }

    /**
     * ingredientQuantity가 "0.5 slices", "2.5 ml", "1 shot", "20 ml" 등의 형식일 때,
     * 수량과 단위를 파싱하여 ml 단위의 양으로 환산합니다.
     */
    private fun parseIngredientQuantity(input: String): Double? {
        val tokens = input.trim().split(" ")
        if (tokens.isEmpty()) return null

        // 첫 번째 토큰: 수량 (분수 또는 소수점)
        val quantityValue = parseFraction(tokens[0]) ?: return null

        // 두 번째 토큰: 단위 (있다면), 없으면 기본 단위 "ml"로 간주
        val unit = if (tokens.size > 1) tokens[1].trim().lowercase() else "ml"

        // 단위별 환산 계수 (필요에 따라 실제 값으로 조정)
        val conversionFactor = when (unit) {
            "ml" -> 1.0
            "shot" -> 30.0        // 예: 1 shot = 30 ml
            "dash" -> 1.0         // 예: 1 dash = 1 ml
            "oz" -> 29.57         // 예: 1 oz = 29.57 ml
            "slice", "slices" -> 15.0  // 예: 1 slice = 15 ml (가정)
            "leaf", "leaves" -> 5.0    // 예: 1 leaf = 5 ml (가정)
            else -> 1.0
        }
        return quantityValue * conversionFactor
    }

    // parseFraction: "1/2" 또는 "2.5"와 같은 문자열을 Double로 변환
    private fun parseFraction(input: String): Double? {
        return if (input.contains("/")) {
            val parts = input.split("/")
            if (parts.size == 2) {
                val numerator = parts[0].trim().toDoubleOrNull()
                val denominator = parts[1].trim().toDoubleOrNull()
                if (numerator != null && denominator != null && denominator != 0.0) {
                    numerator / denominator
                } else {
                    null
                }
            } else {
                null
            }
        } else {
            input.trim().toDoubleOrNull()
        }
    }

    // 레시피 등록 및 수정 모드
    private val _recipeMode = MutableLiveData<CommonUtils.CustomCocktailRecipeMode>()
    val recipeMode: LiveData<CommonUtils.CustomCocktailRecipeMode> get() = _recipeMode

    fun setRecipeMode(mode: CommonUtils.CustomCocktailRecipeMode) {
        _recipeMode.value = mode
    }

    private val _selectedZzimRadioId = MutableLiveData<Int>()
    val selectedZzimRadioId: LiveData<Int> = _selectedZzimRadioId

    private val _selectedTimeRadioId = MutableLiveData<Int>()
    val selectedTimeRadioId: LiveData<Int> = _selectedTimeRadioId

    private val _selectedAlcoholRadioId = MutableLiveData<Int>()
    val selectedAlcoholRadioId: LiveData<Int> = _selectedAlcoholRadioId


    // 필터 관련 코드 추가
    private val _selectedZzimFilter = MutableLiveData<Int?>()
    val selectedZzimFilter: LiveData<Int?> = _selectedZzimFilter

    private val _selectedTimeFilter = MutableLiveData<Int?>()
    val selectedTimeFilter: LiveData<Int?> = _selectedTimeFilter

    private val _selectedAlcoholFilter = MutableLiveData<Int?>()
    val selectedAlcoholFilter: LiveData<Int?> = _selectedAlcoholFilter

    private val _selectedBaseFilter = MutableLiveData<String>()
    val selectedBaseFilter: LiveData<String> = _selectedBaseFilter

    private val _filterSelectedList =
        MutableLiveData<List<Boolean>>(listOf(false, false, false, false))
    val filterSelectedList: LiveData<List<Boolean>> get() = _filterSelectedList

    private val _isBaseFromHome = MutableLiveData<Boolean>()
    val isBaseFromHome: LiveData<Boolean>
        get() = _isBaseFromHome

    fun setFilterSelectedList(newFilter: List<Boolean>){
        _filterSelectedList.postValue(newFilter)
    }

    var zzimButtonSelected: Boolean
        get() = handle.get("zzimButtonSelected") ?: false
        set(value) {
            handle.set("zzimButtonSelected", value)
        }

    var timeButtonSelected: Boolean
        get() = handle.get("timeButtonSelected") ?: false
        set(value) {
            handle.set("timeButtonSelected", value)
        }

    var alcoholButtonSelected: Boolean
        get() = handle.get("alcoholButtonSelected") ?: false
        set(value) {
            handle.set("alcoholButtonSelected", value)
        }

    var baseButtonSelected: Boolean
        get() = handle.get("baseButtonSelected") ?: false
        set(value) {
            handle.set("baseButtonSelected", value)
        }

    // 라디오 버튼 ID 설정 함수 수정
    fun setZzimFilter(radioButtonId: Int) {
        _selectedZzimFilter.value = radioButtonId
        _selectedZzimRadioId.value = radioButtonId  // 선택된 라디오 버튼 ID 저장
        clearOtherFilters("zzim")
    }

    fun setTimeFilter(radioButtonId: Int) {
        _selectedTimeFilter.value = radioButtonId
        _selectedTimeRadioId.value = radioButtonId  // 선택된 라디오 버튼 ID 저장
        clearOtherFilters("time")
    }

    fun setAlcoholFilter(radioButtonId: Int) {
        _selectedAlcoholFilter.value = radioButtonId
        _selectedAlcoholRadioId.value = radioButtonId  // 선택된 라디오 버튼 ID 저장
        clearOtherFilters("alcohol")
    }

    fun setBaseFilter(baseSpirit: String) {
        _selectedBaseFilter.value = baseSpirit
        clearOtherFilters("base")
    }

    fun setBaseFromHome(isFromHome: Boolean){
        _isBaseFromHome.value = isFromHome
    }

    private fun clearOtherFilters(selected: String) {
        if (selected != "zzim") {
            _selectedZzimFilter.value = -1
            updateZzimButtonState(false)
        }
        if (selected != "time") {
            _selectedTimeFilter.value = -1
            updateTimeButtonState(false)
        }
        if (selected != "alcohol") {
            _selectedAlcoholFilter.value = -1
            updateAlcoholButtonState(false)
        }
        if (selected != "base") {
            _selectedBaseFilter.value = ""
            updateBaseButtonState(false)
        }
        // 선택한 필터는 true로 유지
        when (selected) {
            "zzim" -> {
                updateZzimButtonState(true)
                _filterSelectedList.value = listOf(true, false, false, false)
            }

            "time" -> {
                updateTimeButtonState(true)
                _filterSelectedList.value = listOf(false, true, false, false)
            }

            "alcohol" -> {
                updateAlcoholButtonState(true)
                _filterSelectedList.value = listOf(false, false, true, false)
            }

            "base" -> {
                updateBaseButtonState(true)
                _filterSelectedList.value = listOf(false, false, false, true)
            }
        }

        Log.d(
            TAG,
            "Filter clearOtherFilters: Zzim: $zzimButtonSelected, Time: $timeButtonSelected, Alcohol: $alcoholButtonSelected, Base: $baseButtonSelected"
        )
        Log.d(TAG, "Filter clearOtherFilters: filterList ${_filterSelectedList.value}")
        Log.d(
            TAG,
            "Filter clearOtherFilters: Zzim: ${_selectedZzimFilter.value}, Time: ${_selectedTimeFilter.value}, Alcohol: ${_selectedAlcoholFilter.value}, Base: ${_selectedBaseFilter.value}"
        )
    }

    fun setFilterClear() {
        _filterSelectedList.value = listOf(false, false, false, false)
        _selectedZzimFilter.value = -1
        _selectedTimeFilter.value = -1
        _selectedAlcoholFilter.value = -1
        _selectedBaseFilter.value = ""

        // 라디오 버튼 선택 상태도 초기화
        _selectedZzimRadioId.value = -1
        _selectedTimeRadioId.value = -1
        _selectedAlcoholRadioId.value = -1
    }

    fun updateZzimButtonState(selected: Boolean) {
        zzimButtonSelected = selected
//        Log.d(TAG, "Filter clear updateZzimButtonState: $selected")
    }

    fun updateTimeButtonState(selected: Boolean) {
        timeButtonSelected = selected
//        Log.d(TAG, "Filter clear updateTimeButtonState: $selected")
    }

    fun updateAlcoholButtonState(selected: Boolean) {
        alcoholButtonSelected = selected
//        Log.d(TAG, "Filter clear updateAlcoholButtonState: $selected")
    }

    fun updateBaseButtonState(selected: Boolean) {
        baseButtonSelected = selected
//        Log.d(TAG, "Filter clear updateBaseButtonState: $selected")
    }

    private val bartenderService = RetrofitUtil.bartenderService

    // 메시지 리스트
    private val _messages = MutableLiveData<List<ChatMessage>>(emptyList())
    val messages: LiveData<List<ChatMessage>> get() = _messages

    // 메시지 추가
    fun addMessage(message: ChatMessage) {
        val currentList = _messages.value?.toMutableList() ?: mutableListOf()
        currentList.add(message)
        _messages.postValue(currentList)
    }

    private fun getCurrentTime(): String {
        val sdf = java.text.SimpleDateFormat("aa hh:mm", java.util.Locale.getDefault())
        return sdf.format(java.util.Date())
    }

    fun receiveBartenderGreeting() {
        viewModelScope.launch {
            try {
                val response = bartenderService.receiveFromBartender()
                if (response.isSuccessful && response.body() != null) {
                    val bartenderResponse = response.body()!!

                    val newMessage = ChatMessage(
                        message = bartenderResponse.message,
                        isUser = false,
                        timestamp = getCurrentTime(),
                        cocktail = if (bartenderResponse.cocktailRecommendation) bartenderResponse.cocktail else null
                    )
                    addMessage(newMessage) // ✅ 바텐더 첫 인삿말 추가
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun sendMessageToBartender(text: String) {
        val userMessage = ChatMessage(
            message = text,
            isUser = true,
            timestamp = getCurrentTime()
        )
        addMessage(userMessage) // ✅ 사용자가 입력한 메시지를 먼저 추가

        viewModelScope.launch {
            try {
                val response = bartenderService.sendToBartender(BartenderRequest(text))
                if (response.isSuccessful && response.body() != null) {
                    val bartenderResponse = response.body()!!

                    val bartenderMessage = ChatMessage(
                        message = bartenderResponse.message,
                        isUser = false,
                        timestamp = getCurrentTime(),
                        cocktail = if (bartenderResponse.cocktailRecommendation) bartenderResponse.cocktail else null
                    )
                    addMessage(bartenderMessage) // ✅ 바텐더 응답 추가
                } else {
                    Log.d(
                        TAG,
                        "sendMessageToBartender: 서버 오류 : ${response.code()} - ${response.message()}"
                    )
                    Log.d(
                        TAG,
                        "sendMessageToBartender: 서버 응답 바디 : ${response.errorBody()?.string()}"
                    )
                    addMessage(
                        ChatMessage(
                            "⚠️ 서버 오류 발생 (${response.code()})",
                            false,
                            getCurrentTime()
                        )
                    )
                }
            } catch (e: Exception) {
                Log.e("BartenderAPI", "네트워크 오류 발생", e)
                addMessage(ChatMessage("⚠️ 네트워크 오류 발생", false, getCurrentTime()))
            }
        }
    }

    fun getRecommendedCocktailId() {
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.recommendedCocktailService.getRecommendedCocktail(userId, 5)
            }.onSuccess {
                Log.d(TAG, "getRecommendedCocktailId: ${it.recommended_cocktails}")
                setCocktailId(it.recommended_cocktails)
            }.onFailure {
                Log.d(TAG, "getRecommendedCocktailId: ${it.message}")
            }
        }
    }

    private val _isBottomSheetClosed = MutableLiveData<Boolean>()
    val isBottomSheetClosed: LiveData<Boolean> get() = _isBottomSheetClosed

    fun setBottomSheetClosed(isClosed: Boolean) {
        _isBottomSheetClosed.value = isClosed
    }
}