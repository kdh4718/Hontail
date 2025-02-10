package com.hontail.ui.custom

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.hontail.data.local.IngredientRepository
import com.hontail.data.model.dto.IngredientsTable

class CustomCocktailIngredientDetailViewModel(private val handle: SavedStateHandle): ViewModel() {

    private val ingredientRepository = IngredientRepository.getInstance()

    // ingredientId는 SavedStateHandle에 저장되며, getLiveData로 관찰할 수 있음.
    var ingredientId: Int
        get() = handle.get<Int>("ingredientId") ?: 0
        set(value) {
            handle.set("ingredientId", value)
        }

    // ingredientId가 변경되면, switchMap을 통해 Repository의 LiveData를 반환하도록 함.
    val ingredientDetailInfo: LiveData<IngredientsTable> =
        handle.getLiveData<Int>("ingredientId").switchMap { id ->
            if (id != 0) {
                ingredientRepository.getIngredientsById(id)
            } else {
                // id가 0인 경우 빈 LiveData 반환
                MutableLiveData<IngredientsTable>()
            }
        }

}