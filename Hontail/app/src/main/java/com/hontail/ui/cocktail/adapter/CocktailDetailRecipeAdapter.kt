package com.hontail.ui.cocktail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hontail.databinding.ListItemCocktailDetailRecipeItemBinding
import com.hontail.ui.cocktail.screen.Recipe

class CocktailDetailRecipeAdapter(private val items: List<Recipe>): RecyclerView.Adapter<CocktailDetailRecipeAdapter.CocktailDetailRecipeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CocktailDetailRecipeViewHolder {
        val binding = ListItemCocktailDetailRecipeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CocktailDetailRecipeViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: CocktailDetailRecipeViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class CocktailDetailRecipeViewHolder(private val binding: ListItemCocktailDetailRecipeItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Recipe) {

            binding.apply {

                textViewCocktailDetailRecipeNumber.text = item.recipeNumber.toString()
                textViewCocktailDetailRecipeContent.text = item.recipeContent
            }
        }
    }
}