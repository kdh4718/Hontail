package com.hontail.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hontail.data.model.request.Recipe
import com.hontail.data.model.response.Cocktail
import com.hontail.data.remote.RetrofitUtil
import com.hontail.ui.custom.screen.CustomCocktailItem
import com.hontail.util.CommonUtils
import kotlinx.coroutines.launch

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

    fun setCocktailId(cocktailId: Int){
        _cocktailId.postValue(cocktailId)
    }

    fun setIngredientList(ingredientList: List<String>){
        _ingredientList.postValue(ingredientList)
    }

    // 선택된 칵테일 베이스주 타입
    private val _baseSpirit = MutableLiveData<String>("")
    val baseSpirit: LiveData<String>
        get() = _baseSpirit

    fun setBaseSpirit(baseSpirit: String){
        _baseSpirit.postValue(baseSpirit)
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
            if(position in steps.indices) {
                val updatedList = steps.toMutableList()
                updatedList[position] = Recipe(newAction, newGuide, updatedList[position].sequence)
                _recipeSteps.postValue(updatedList)
            }
        }
    }

    // -------------------------------------------
    // 도수(알코올 농도) 계산 (가중 평균 방식)
    // 전체 도수 = (Σ (각 재료의 alcoholContent × 환산된 수량(ml))) / (Σ 환산된 수량(ml))
    // -------------------------------------------
    private val _overallAlcoholContent = MediatorLiveData<Double>()
    val overallAlcoholContent: LiveData<Double> get() = _overallAlcoholContent

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
            Log.d(TAG, "computeOverallAlcoholContent: ${ingredient.ingredientName} ${ingredient.alcoholContent}")
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

    // 필터 관련 코드 추가
    private val _selectedZzimFilter = MutableLiveData<Int?>()
    val selectedZzimFilter: LiveData<Int?> = _selectedZzimFilter

    private val _selectedTimeFilter = MutableLiveData<Int?>()
    val selectedTimeFilter: LiveData<Int?> = _selectedTimeFilter

    private val _selectedAlcoholFilter = MutableLiveData<Int?>()
    val selectedAlcoholFilter: LiveData<Int?> = _selectedAlcoholFilter

    private val _selectedBaseFilter = MutableLiveData<Int?>()
    val selectedBaseFilter: LiveData<Int?> = _selectedBaseFilter

    private val _zzimButtonSelected = MutableLiveData<Boolean>()
    val zzimButtonSelected: LiveData<Boolean> = _zzimButtonSelected

    private val _timeButtonSelected = MutableLiveData<Boolean>()
    val timeButtonSelected: LiveData<Boolean> = _timeButtonSelected

    private val _alcoholButtonSelected = MutableLiveData<Boolean>()
    val alcoholButtonSelected: LiveData<Boolean> = _alcoholButtonSelected

    private val _baseButtonSelected = MutableLiveData<Boolean>()
    val baseButtonSelected: LiveData<Boolean> = _baseButtonSelected

    fun setZzimFilter(radioButtonId: Int) {
        _selectedZzimFilter.value = radioButtonId
    }

    fun setTimeFilter(radioButtonId: Int) {
        _selectedTimeFilter.value = radioButtonId
    }

    fun setAlcoholFilter(radioButtonId: Int) {
        _selectedAlcoholFilter.value = radioButtonId
    }

    fun setBaseFilter(radioButtonId: Int) {
        _selectedBaseFilter.value = radioButtonId
    }

    fun updateZzimButtonState(selected: Boolean) {
        _zzimButtonSelected.value = selected
    }

    fun updateTimeButtonState(selected: Boolean) {
        _timeButtonSelected.value = selected
    }

    fun updateAlcoholButtonState(selected: Boolean) {
        _alcoholButtonSelected.value = selected
    }

    fun updateBaseButtonState(selected: Boolean) {
        _baseButtonSelected.value = selected
    }
}