package com.hontail.ui.ingredient

import android.content.Context
import android.content.res.Resources
import android.graphics.Rect
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hontail.R
import com.hontail.base.BaseFragment
import com.hontail.databinding.FragmentIngredientListBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.MainActivityViewModel

class IngredientListFragment : BaseFragment<FragmentIngredientListBinding>(
    FragmentIngredientListBinding::bind,
    R.layout.fragment_ingredient_list
) {
    private lateinit var mainActivity: MainActivity
    private val activityViewModel: MainActivityViewModel by activityViewModels()
    private lateinit var ingredientAdapter: IngredientAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadDummyData()
    }

    private fun setupRecyclerView() {
        ingredientAdapter = IngredientAdapter()
        recyclerView = view?.findViewById(R.id.recyclerViewIngredients) ?: return
        recyclerView.apply {
            adapter = ingredientAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun loadDummyData() {
        val dummyIngredients = listOf(
            Ingredient("당근", "채소"),
            Ingredient("양파", "채소"),
            Ingredient("돼지고기", "육류"),
            Ingredient("소고기", "육류"),
            Ingredient("계란", "기타"),
            Ingredient("우유", "유제품")
        )
        ingredientAdapter.submitList(dummyIngredients)
    }
}

data class Ingredient(
    val name: String,
    val category: String
)