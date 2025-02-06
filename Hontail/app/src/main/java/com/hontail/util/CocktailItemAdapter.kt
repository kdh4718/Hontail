package com.hontail.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hontail.databinding.ListItemCocktailBinding
import com.hontail.ui.mypage.Cocktail

class CocktailItemAdapter(private val items: List<Cocktail>): RecyclerView.Adapter<CocktailItemAdapter.CocktailItemViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CocktailItemViewHolder {
        val binding = ListItemCocktailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CocktailItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: CocktailItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class CocktailItemViewHolder(private val binding: ListItemCocktailBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Cocktail) {

            binding.apply {
                textViewListItemCocktailName.text = item.cocktailName
                textViewListItemCocktailBaseSpirit.text = item.cocktailBaseSpirit
                textViewListItemCocktailIngredientCount.text = "재료 ${item.cocktailIngredientCnt}개"
                textViewListItemCocktailTotalZzim.text = CommonUtils.makeComma(item.cocktailZzimCnt)
                textViewListItemCocktailAlcoholContent.text = "${item.cocktailAlcholContent}%"
            }
        }
    }

}