package com.hontail.ui.custom.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hontail.databinding.ListItemCustomCocktailBottomSheetUnitBinding
import com.hontail.ui.custom.screen.UnitItem

class CustomCocktailBottomSheetAdapter(private val items: List<UnitItem>): RecyclerView.Adapter<CustomCocktailBottomSheetAdapter.CustomCocktailBottomSheetUnitViewHolder>() {

    lateinit var customCocktailBottomSheetListener: ItemOnClickListener

    interface ItemOnClickListener {
        fun onClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomCocktailBottomSheetUnitViewHolder {
        val binding = ListItemCustomCocktailBottomSheetUnitBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomCocktailBottomSheetUnitViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: CustomCocktailBottomSheetUnitViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    inner class CustomCocktailBottomSheetUnitViewHolder(private val binding: ListItemCustomCocktailBottomSheetUnitBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: UnitItem, position: Int) {

            binding.apply {

                textViewListItemCustomCocktailBottomSheetUnitName.text = item.unitName

                if(item.unitSelected) {
                    imageViewListItemCustomCocktailBottomSheetUnitCheck.visibility = View.VISIBLE
                }
                else {
                    imageViewListItemCustomCocktailBottomSheetUnitCheck.visibility = View.INVISIBLE
                }

                root.setOnClickListener {
                    customCocktailBottomSheetListener.onClick(position)
                }
            }
        }
    }
}