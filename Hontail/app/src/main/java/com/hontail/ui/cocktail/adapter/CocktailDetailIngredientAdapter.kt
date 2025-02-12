package com.hontail.ui.cocktail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hontail.data.model.response.CocktailIngredient
import com.hontail.databinding.ListItemCocktailDetailIngredientItemBinding

class CocktailDetailIngredientAdapter(private val items: List<CocktailIngredient>): RecyclerView.Adapter<CocktailDetailIngredientAdapter.CocktailDetailIngredientViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CocktailDetailIngredientViewHolder {
        val binding = ListItemCocktailDetailIngredientItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CocktailDetailIngredientViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: CocktailDetailIngredientViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class CocktailDetailIngredientViewHolder(private val binding: ListItemCocktailDetailIngredientItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CocktailIngredient) {

            binding.apply {

                textViewCocktailDetailIngredientName.text = item.ingredient.ingredientNameKor
                textViewCocktailDetailIngredientAmount.text = item.ingredientQuantity
            }
        }
    }
}