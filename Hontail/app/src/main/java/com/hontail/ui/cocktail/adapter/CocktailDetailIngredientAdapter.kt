package com.hontail.ui.cocktail.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hontail.R
import com.hontail.data.model.response.CocktailIngredient
import com.hontail.databinding.ListItemCocktailDetailIngredientItemBinding

class CocktailDetailIngredientAdapter(private val context: Context, private val items: List<CocktailIngredient>): RecyclerView.Adapter<CocktailDetailIngredientAdapter.CocktailDetailIngredientViewHolder>() {

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
                Glide.with(context)
                    .load(item.ingredient.ingredientImage)
                    .placeholder(R.drawable.ic_bottom_navi_zzim_selected)
                    .error(R.drawable.ic_bottom_navi_zzim_unselected)
                    .into(imageViewCocktailDetailIngredientImage)

                textViewCocktailDetailIngredientName.text = item.ingredient.ingredientNameKor
                textViewCocktailDetailIngredientAmount.text = item.ingredientQuantity
            }
        }
    }
}