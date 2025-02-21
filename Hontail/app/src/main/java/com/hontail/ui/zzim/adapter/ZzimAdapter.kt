package com.hontail.ui.zzim.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.helper.widget.Grid
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.api.Distribution.BucketOptions.Linear
import com.hontail.data.model.response.CocktailListResponse
import com.hontail.data.model.response.LikedCocktail
import com.hontail.databinding.ListItemZzimEmptyBinding
import com.hontail.databinding.ListItemZzimLikedBinding
import com.hontail.databinding.ListItemZzimRecentViewedBinding
import com.hontail.ui.zzim.screen.ZzimItem
import com.hontail.util.CocktailItemAdapter

class ZzimAdapter(private val context: Context, private var items: List<ZzimItem>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var zzimListListener: ItemOnClickListener

    interface ItemOnClickListener{
        fun onClickCocktailItem(cocktailId: Int)
    }

    companion object {
        const val VIEW_TYPE_LIKED = 0
        const val VIEW_TYPE_RECENT_VIEWED = 1
        const val VIEW_TYPE_EMPTY = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when(items[position]) {
            is ZzimItem.LikedList -> VIEW_TYPE_LIKED
            is ZzimItem.RecentViewedList -> VIEW_TYPE_RECENT_VIEWED
            is ZzimItem.Empty -> VIEW_TYPE_EMPTY
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when(viewType) {

            VIEW_TYPE_LIKED -> {
                val binding = ListItemZzimLikedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ZzimLikedViewHolder(binding)
            }

            VIEW_TYPE_RECENT_VIEWED -> {
                val binding = ListItemZzimRecentViewedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ZzimRecentViewedViewHolder(binding)
            }

            VIEW_TYPE_EMPTY -> {
                val binding = ListItemZzimEmptyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ZzimEmptyViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Unknown ViewType: $viewType")
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when(val item = items[position]) {
            is ZzimItem.LikedList -> (holder as ZzimLikedViewHolder).bind(item.likedList)
            is ZzimItem.RecentViewedList -> (holder as ZzimRecentViewedViewHolder).bind(item.recentList)
            is ZzimItem.Empty -> (holder as ZzimEmptyViewHolder).bind()
        }
    }

    fun updateItems(newItems: List<ZzimItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    // 찜한 리스트
    inner class ZzimLikedViewHolder(private val binding: ListItemZzimLikedBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(zzimLikedList: List<CocktailListResponse>) {

            binding.apply {

                val zzimLikedAdapter = CocktailItemAdapter(context, zzimLikedList)

                recyclerViewListITemZzimLiked.layoutManager = GridLayoutManager(context, 2)
                recyclerViewListITemZzimLiked.adapter = zzimLikedAdapter

                zzimLikedAdapter.cocktailItemListener = object : CocktailItemAdapter.ItemOnClickListener{
                    override fun onClickCocktailItem(cocktailId: Int) {
                        zzimListListener.onClickCocktailItem(cocktailId)
                    }
                }
            }
        }
    }

    // 최근 본 상품 리스트
    inner class ZzimRecentViewedViewHolder(private val binding: ListItemZzimRecentViewedBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(zzimRecentViewedList: List<CocktailListResponse>) {

            binding.apply {

                val zzimRecentViewedAdapter = CocktailItemAdapter(context, zzimRecentViewedList)

                recyclerViewListITemZzimRecentViewed.layoutManager = GridLayoutManager(context, 2)
                recyclerViewListITemZzimRecentViewed.adapter = zzimRecentViewedAdapter

                zzimRecentViewedAdapter.cocktailItemListener = object : CocktailItemAdapter.ItemOnClickListener{
                    override fun onClickCocktailItem(cocktailId: Int) {
                        zzimListListener.onClickCocktailItem(cocktailId)
                    }
                }
            }
        }
    }

    // 찜한 리스트 비었을 때
    inner class ZzimEmptyViewHolder(private val binding: ListItemZzimEmptyBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            binding.apply {

            }
        }
    }
}