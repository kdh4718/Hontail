package com.hontail.ui.custom

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hontail.databinding.ListItemCustomCocktailBottomSheetUnitBinding

class CustomCocktailBottomSheetAdapter(private val items: List<UnitItem>): RecyclerView.Adapter<CustomCocktailBottomSheetAdapter.CustomCocktailBottomSheetUnitViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomCocktailBottomSheetAdapter.CustomCocktailBottomSheetUnitViewHolder {
        val binding = ListItemCustomCocktailBottomSheetUnitBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomCocktailBottomSheetUnitViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: CustomCocktailBottomSheetAdapter.CustomCocktailBottomSheetUnitViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class CustomCocktailBottomSheetUnitViewHolder(private val binding: ListItemCustomCocktailBottomSheetUnitBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: UnitItem) {

            binding.apply {

                textViewListItemCustomCocktailBottomSheetUnitName.text = item.unitName

                if(item.unitSelected) {
                    imageViewListItemCustomCocktailBottomSheetUnitCheck.visibility = View.VISIBLE
                }
            }
        }
    }
}