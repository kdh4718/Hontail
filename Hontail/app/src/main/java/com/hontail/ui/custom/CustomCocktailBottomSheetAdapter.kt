package com.hontail.ui.custom

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hontail.databinding.ListItemCustomCocktailBottomSheetUnitBinding

class CustomCocktailBottomSheetAdapter(
    private val unitList: MutableList<UnitItem>,
    private val onUnitSelected: (String) -> Unit
) : RecyclerView.Adapter<CustomCocktailBottomSheetAdapter.CustomCocktailBottomSheetUnitViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomCocktailBottomSheetUnitViewHolder {
        val binding = ListItemCustomCocktailBottomSheetUnitBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CustomCocktailBottomSheetUnitViewHolder(binding)
    }

    override fun getItemCount(): Int = unitList.size

    override fun onBindViewHolder(holder: CustomCocktailBottomSheetUnitViewHolder, position: Int) {
        holder.bind(unitList[position])
    }

    inner class CustomCocktailBottomSheetUnitViewHolder(
        private val binding: ListItemCustomCocktailBottomSheetUnitBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: UnitItem) {
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
            }
        }
    }
}