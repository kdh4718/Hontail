package com.hontail.ui.custom

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hontail.databinding.ListItemCustomCocktailBottomSheetUnitBinding

class CustomCocktailBottomSheetAdapter(private val items: List<UnitItem>): RecyclerView.Adapter<CustomCocktailBottomSheetAdapter.CustomCocktailBottomSheetUnitViewHolder>() {

    lateinit var customCocktailBottomSheetListener: ItemOnClickListener

    interface ItemOnClickListener {
        fun onClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomCocktailBottomSheetAdapter.CustomCocktailBottomSheetUnitViewHolder {
        val binding = ListItemCustomCocktailBottomSheetUnitBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomCocktailBottomSheetUnitViewHolder(binding)
    }

    override fun getItemCount(): Int = unitList.size

    override fun onBindViewHolder(holder: CustomCocktailBottomSheetUnitViewHolder, position: Int) {
        holder.bind(unitList[position])
    }

    override fun onBindViewHolder(holder: CustomCocktailBottomSheetAdapter.CustomCocktailBottomSheetUnitViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    inner class CustomCocktailBottomSheetUnitViewHolder(private val binding: ListItemCustomCocktailBottomSheetUnitBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: UnitItem, position: Int) {

            binding.apply {
                textViewListItemCustomCocktailBottomSheetUnitName.text = item.unitName

                // 체크 표시 visibility 설정
                imageViewListItemCustomCocktailBottomSheetUnitCheck.visibility =
                    if (item.unitSelected) View.VISIBLE else View.GONE

                root.setOnClickListener {
                    // 모든 아이템 선택 해제
                    unitList.forEach { it.unitSelected = false }

                    // 현재 아이템만 선택
                    item.unitSelected = true

                    // 전체 리스트 갱신
                    notifyDataSetChanged()

                    // 선택된 unit 전달
                    onUnitSelected(item.unitName)
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