package com.hontail.ui.cocktail.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hontail.data.model.dto.SearchHistoryTable
import com.hontail.databinding.ListItemCocktailSearchRecentItemBinding
import com.hontail.ui.cocktail.screen.RecentItem

private const val TAG = "CocktailSearchRecentAda"
class CocktailSearchRecentAdapter(private val items: List<SearchHistoryTable>): RecyclerView.Adapter<CocktailSearchRecentAdapter.CocktailSearchRecentViewHolder>() {

    lateinit var cocktailSearchRecentItemListener: ItemOnClickListener

    interface ItemOnClickListener {
        fun onClickRecentDelete()
        fun onClickRecentItem()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CocktailSearchRecentViewHolder {
        val binding = ListItemCocktailSearchRecentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CocktailSearchRecentViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: CocktailSearchRecentViewHolder, position: Int) {
        holder.bind(items[position])
        Log.d(TAG, "onBindViewHolder: $position")
        
    }

    inner class CocktailSearchRecentViewHolder(private val binding: ListItemCocktailSearchRecentItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SearchHistoryTable) {

            binding.apply {

                textViewListItemCocktailRecentItemName.text = item.searchHistory

                // 최근 검색 아이템으로 들어가기.
                textViewListItemCocktailRecentItemName.setOnClickListener {
                    cocktailSearchRecentItemListener.onClickRecentItem()
                }

                // 최근 검색 아이템 삭제하기.
                imageViewListItemCocktailRecentItemDelete.setOnClickListener {
                    cocktailSearchRecentItemListener.onClickRecentDelete()
                }
            }
        }
    }
}