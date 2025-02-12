package com.hontail.ui.cocktail.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.hontail.data.model.response.CocktailListResponse
import com.hontail.databinding.ListItemCocktailListCocktailItemBinding
import com.hontail.databinding.ListItemCocktailListFilterBinding
import com.hontail.databinding.ListItemCocktailListSearchBarBinding
import com.hontail.databinding.ListItemCocktailListTabLayoutBinding
import com.hontail.ui.cocktail.screen.CocktailListItem
import com.hontail.util.CocktailItemAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest

class CocktailListAdapter(
    private val context: Context,
    private var items: MutableList<CocktailListItem>,
    private val lifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var cocktailListListener: ItemOnClickListener

    interface ItemOnClickListener {
        fun onClickRandom()
        fun onClickCocktailItem(cocktailId: Int)
        fun onClickSearch()
        fun onClickTab(position: Int)
        fun onClickFilter(position: Int)
    }

    companion object {
        const val VIEW_TYPE_SEARCH_BAR = 0
        const val VIEW_TYPE_TAB_LAYOUT = 1
        const val VIEW_TYPE_FILTER = 2
        const val VIEW_TYPE_COCKTAIL_ITEMS = 3
    }

    // Adapter를 미리 생성하여 ViewHolder에서 재사용 (핵심 변경 사항)
    private val cocktailItemAdapter = CocktailItemAdapter(context)

    private val filters = listOf("찜", "시간", "도수", "베이스주")

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is CocktailListItem.SearchBar -> VIEW_TYPE_SEARCH_BAR
            is CocktailListItem.TabLayout -> VIEW_TYPE_TAB_LAYOUT
            is CocktailListItem.Filter -> VIEW_TYPE_FILTER
            is CocktailListItem.CocktailItems -> VIEW_TYPE_COCKTAIL_ITEMS
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_SEARCH_BAR -> {
                val binding = ListItemCocktailListSearchBarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CocktailListSearchBarViewHolder(binding)
            }

            VIEW_TYPE_TAB_LAYOUT -> {
                val binding = ListItemCocktailListTabLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CocktailListTabLayoutViewHolder(binding)
            }

            VIEW_TYPE_FILTER -> {
                val binding = ListItemCocktailListFilterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CocktailListFilterViewHolder(binding)
            }

            VIEW_TYPE_COCKTAIL_ITEMS -> {
                val binding = ListItemCocktailListCocktailItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CocktailListCocktailItemsViewHolder(binding, cocktailItemAdapter, lifecycleOwner)
            }

            else -> throw IllegalArgumentException("Unknown ViewType: $viewType")
        }
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is CocktailListItem.SearchBar -> (holder as CocktailListSearchBarViewHolder).bind()
            is CocktailListItem.TabLayout -> (holder as CocktailListTabLayoutViewHolder).bind()
            is CocktailListItem.Filter -> (holder as CocktailListFilterViewHolder).bind(item.filters)
            is CocktailListItem.CocktailItems -> (holder as CocktailListCocktailItemsViewHolder).bind(item.cocktails)
        }
    }

    // Search Bar ViewHolder
    inner class CocktailListSearchBarViewHolder(private val binding: ListItemCocktailListSearchBarBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.constraintLayoutListItemCocktailListSearch.setOnClickListener {
                cocktailListListener.onClickSearch()
            }
            binding.imageViewCocktailListRandom.setOnClickListener {
                cocktailListListener.onClickRandom()
            }
        }
    }

    // Tab Layout
    inner class CocktailListTabLayoutViewHolder(private val binding: ListItemCocktailListTabLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.apply {
                tabLayoutCocktailList.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab?) {
                        cocktailListListener.onClickTab(tab?.position ?: 0)
                    }

                    override fun onTabUnselected(tab: TabLayout.Tab?) {

                    }

                    override fun onTabReselected(tab: TabLayout.Tab?) {

                    }
                })
            }
        }
    }

    // Filter
    inner class CocktailListFilterViewHolder(private val binding: ListItemCocktailListFilterBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(filters: List<String>) {
            binding.apply {
                val cocktailListFilterAdapter = CocktailListFilterAdapter(filters)

                recyclerViewListItemCocktailListFilter.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                recyclerViewListItemCocktailListFilter.adapter = cocktailListFilterAdapter

                cocktailListFilterAdapter.cocktailListFilterListener = object : CocktailListFilterAdapter.ItemOnClickListener {
                    override fun onClickFilter(position: Int) {
                        cocktailListListener.onClickFilter(position)
                    }
                }
            }
        }
    }

    // Cocktail Items ViewHolder (핵심 변경 사항 적용됨)
    inner class CocktailListCocktailItemsViewHolder(
        private val binding: ListItemCocktailListCocktailItemBinding,
        private val cocktailItemAdapter: CocktailItemAdapter,
        private val lifecycleOwner: LifecycleOwner
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.recyclerViewListItemCocktailListCocktailItem.apply {
                layoutManager = GridLayoutManager(context, 2)
                adapter = cocktailItemAdapter
            }
        }

        fun bind(pagingData: PagingData<CocktailListResponse>) {
            cocktailItemAdapter.submitData(lifecycleOwner.lifecycle, pagingData)
        }
    }

    fun submitData(newItem: PagingData<CocktailListResponse>) {
        lifecycleOwner.lifecycleScope.launch {
            newItem
                .let { pagingData ->
                    val newItems = mutableListOf<CocktailListItem>()

                    // 기존 상단 UI 요소 유지
                    newItems.add(CocktailListItem.SearchBar)
                    newItems.add(CocktailListItem.TabLayout)
                    newItems.add(CocktailListItem.Filter(filters))

                    // 새로운 칵테일 리스트 추가
                    newItems.add(CocktailListItem.CocktailItems(pagingData))

                    items.clear()
                    items.addAll(newItems)
                    notifyDataSetChanged()
                }
        }
    }
}
