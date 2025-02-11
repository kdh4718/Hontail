package com.hontail.ui.custom.adapter

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.hontail.R
import com.hontail.data.model.request.Recipe
import com.hontail.databinding.ListItemCustomCocktailRecipeStepItemBinding

private const val TAG = "CustomCocktailRecipeSte"
class CustomCocktailRecipeStepAdapter(var items: MutableList<Recipe>) :
    RecyclerView.Adapter<CustomCocktailRecipeStepAdapter.CustomCocktailRecipeStepViewHolder>() {

    lateinit var customCocktailRecipeStepListener: ItemOnClickListener

    interface ItemOnClickListener {
        fun onClickDelete(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomCocktailRecipeStepViewHolder {
        val binding = ListItemCustomCocktailRecipeStepItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CustomCocktailRecipeStepViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: CustomCocktailRecipeStepViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    inner class CustomCocktailRecipeStepViewHolder(private val binding: ListItemCustomCocktailRecipeStepItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Recipe, position: Int) {
            binding.apply {
                if (position == 0) {
                    imageViewListItemCustomCocktailRecipeStepDelete.visibility = View.GONE
                } else {
                    imageViewListItemCustomCocktailRecipeStepDelete.visibility = View.VISIBLE
                }

                textViewListItemCustomCocktailRecipeStepSequence.text = (position + 1).toString()

                textViewListItemCustomCocktailRecipeStepDescription.text = item.recipeGuide

                when(item.recipeAction) {
                    null -> radioButtonListItemCustomCocktailRecipeStepDefault.isChecked = true
                    "stir" -> radioButtonListItemCustomCocktailRecipeStepStir.isChecked = true
                    "pour" -> radioButtonListItemCustomCocktailRecipeStepPour.isChecked = true
                    "shake" -> radioButtonListItemCustomCocktailRecipeStepShake.isChecked = true
                }

                // ✅ 삭제 버튼 클릭 이벤트
                imageViewListItemCustomCocktailRecipeStepDelete.setOnClickListener {
                    customCocktailRecipeStepListener.onClickDelete(position)
                }
            }
        }
    }
}
