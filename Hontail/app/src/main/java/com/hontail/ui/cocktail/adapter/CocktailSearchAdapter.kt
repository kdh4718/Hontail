package com.hontail.ui.cocktail.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hontail.data.model.dto.SearchHistoryTable
import com.hontail.data.model.response.CocktailListResponse
import com.hontail.databinding.ListItemCocktailSearchRecentBinding
import com.hontail.databinding.ListItemCocktailSearchResultBinding
import com.hontail.databinding.ListItemCocktailSearchSearchBarBinding
import com.hontail.ui.cocktail.screen.CocktailSearchItem
import com.hontail.util.CocktailItemAdapter

class CocktailSearchAdapter(
    private val context: Context,
    private var items: MutableList<CocktailSearchItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var cocktailSearchListener: ItemOnClickListener

    interface ItemOnClickListener {
        fun onClickCancel()
        fun onClickSearch(text: String)
        fun onClickCocktailItem(cocktailId: Int)
        fun onClickSearchHistoryDelete(id: Int)
        fun onClickPageDown()
        fun onClickPageUp()
    }

    companion object {
        const val VIEW_TYPE_SEARCH_BAR = 0
        const val VIEW_TYPE_RECENT = 1
        const val VIEW_TYPE_RESULT = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is CocktailSearchItem.SearchBar -> VIEW_TYPE_SEARCH_BAR
            is CocktailSearchItem.Recent -> VIEW_TYPE_RECENT
            is CocktailSearchItem.Result -> VIEW_TYPE_RESULT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {

            VIEW_TYPE_SEARCH_BAR -> {
                val binding = ListItemCocktailSearchSearchBarBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                CocktailSearchBarViewHolder(binding)
            }

            VIEW_TYPE_RECENT -> {
                val binding = ListItemCocktailSearchRecentBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                CocktailRecentViewHolder(binding)
            }

            VIEW_TYPE_RESULT -> {
                val binding = ListItemCocktailSearchResultBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                CocktailResultViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Unknown ViewType: $viewType")
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (val item = items[position]) {
            is CocktailSearchItem.SearchBar -> (holder as CocktailSearchBarViewHolder).bind(item.initSearch)
            is CocktailSearchItem.Recent -> (holder as CocktailRecentViewHolder).bind(item.recentList)
            is CocktailSearchItem.Result -> (holder as CocktailResultViewHolder).bind(item.resultList, item.currentPage, item.totalPage)
        }
    }

    // Search Bar
    inner class CocktailSearchBarViewHolder(private val binding: ListItemCocktailSearchSearchBarBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(initSearch: Boolean) {
            binding.apply {

                if(initSearch) {
                    editTextCocktailSearchBar.requestFocus() // ✅ EditText에 포커스 설정

                    val imm =
                        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

                    editTextCocktailSearchBar.postDelayed({
                        imm.showSoftInput(editTextCocktailSearchBar, InputMethodManager.SHOW_IMPLICIT)
                    }, 100)
                }

                imageViewListItemCocktailSearchBarSearch.setOnClickListener {
                    val query = editTextCocktailSearchBar.text.toString().trim()
                    if (query.isNotEmpty()) {
                        cocktailSearchListener.onClickSearch(query)
                        editTextCocktailSearchBar.text.clear()
                    }
                }

                editTextCocktailSearchBar.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        val query = editTextCocktailSearchBar.text.toString().trim()
                        if (query.isNotEmpty()) { // 검색 실행
                            cocktailSearchListener.onClickSearch(query)
                            editTextCocktailSearchBar.text.clear()

                        }
                        true
                    } else {
                        false
                    }
                    return@OnEditorActionListener true
                })

                // 취소 이벤트
                textViewListItemCocktailSearchBarCancel.setOnClickListener {
                    cocktailSearchListener.onClickCancel()
                }
            }
        }
    }


    // 최근 검색
    inner class CocktailRecentViewHolder(private val binding: ListItemCocktailSearchRecentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(recentList: List<SearchHistoryTable>) {

            binding.apply {

                val cocktailSearchRecentAdapter = CocktailSearchRecentAdapter(recentList)

                recyclerViewListItemCocktailRecent.layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                recyclerViewListItemCocktailRecent.adapter = cocktailSearchRecentAdapter
                recyclerViewListItemCocktailRecent.isNestedScrollingEnabled = false

                cocktailSearchRecentAdapter.cocktailSearchRecentItemListener =
                    object : CocktailSearchRecentAdapter.ItemOnClickListener {
                        override fun onClickRecentDelete(id: Int) {
                            cocktailSearchListener.onClickSearchHistoryDelete(id)
                        }

                        override fun onClickRecentItem(searchText: String) {
                            cocktailSearchListener.onClickSearch(searchText)
                        }
                    }
            }
        }
    }

    // 칵테일 검색 결과
    inner class CocktailResultViewHolder(private val binding: ListItemCocktailSearchResultBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cocktailList: List<CocktailListResponse>, currentPage: Int, totalPage: Int) {

            binding.apply {

                val cocktailSearchResultAdapter = CocktailItemAdapter(context, cocktailList)

                recyclerViewListItemCocktailSearchResult.layoutManager =
                    GridLayoutManager(context, 2)
                recyclerViewListItemCocktailSearchResult.adapter = cocktailSearchResultAdapter

                cocktailSearchResultAdapter.cocktailItemListener =
                    object : CocktailItemAdapter.ItemOnClickListener {
                        // 칵테일 아이템으로 상세화면 가기.
                        override fun onClickCocktailItem(cocktailId: Int) {
                            cocktailSearchListener.onClickCocktailItem(cocktailId)
                        }
                    }

                // 페이지 다운
                imageViewListItemCocktailListCocktailSearchResultPagePreview.setOnClickListener {
                    cocktailSearchListener.onClickPageDown()
                }

                // 페이지 업
                imageViewListItemCocktailListCocktailSearchResultPageNext.setOnClickListener {
                    cocktailSearchListener.onClickPageUp()
                }

                textViewListItemCocktailListCocktailSearchResultPage.text = "${currentPage + 1} / $totalPage"
            }
        }
    }

    fun updateItems(newItems: List<CocktailSearchItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}