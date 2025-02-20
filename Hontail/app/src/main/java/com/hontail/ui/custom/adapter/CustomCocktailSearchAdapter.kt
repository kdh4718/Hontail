package com.hontail.ui.custom.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.content.contentValuesOf
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hontail.R
import com.hontail.databinding.ListItemCustomCocktailSearchBarBinding
import com.hontail.databinding.ListItemCustomCocktailSearchHeaderBinding
import com.hontail.databinding.ListItemCustomCocktailSearchResultBinding
import com.hontail.ui.custom.screen.CustomCocktailSearchItem

private const val TAG = "CustomCocktailSearchAda"
class CustomCocktailSearchAdapter(private var items: MutableList<CustomCocktailSearchItem>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var customCocktailSearchListener: ItemOnClickListener

    var onSearchQueryChanged: ((String) -> Unit)? = null

    interface ItemOnClickListener {
        fun onClickIngredient(ingredientId: Int)
        fun onClickCancel()
    }

    companion object {
        const val VIEW_TYPE_SEARCH_BAR = 0
        const val VIEW_TYPE_HEADER = 1
        const val VIEW_TYPE_INGREDIENT_RESULT = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is CustomCocktailSearchItem.SearchBar -> VIEW_TYPE_SEARCH_BAR
            is CustomCocktailSearchItem.Header -> VIEW_TYPE_HEADER
            is CustomCocktailSearchItem.IngredientResult -> VIEW_TYPE_INGREDIENT_RESULT
            else -> VIEW_TYPE_SEARCH_BAR
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when(viewType) {

            VIEW_TYPE_SEARCH_BAR -> {
                val binding = ListItemCustomCocktailSearchBarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CustomCocktailSearchBarViewHolder(binding)
            }

            VIEW_TYPE_HEADER -> {
                val binding = ListItemCustomCocktailSearchHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CustomCocktailSearchHeaderViewHolder(binding)
            }

            VIEW_TYPE_INGREDIENT_RESULT -> {
                val binding = ListItemCustomCocktailSearchResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CustomCocktailSearchIngredientResultViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Unknown ViewType: $viewType")
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when(holder) {

            is CustomCocktailSearchBarViewHolder -> holder.bind(items[position] as CustomCocktailSearchItem.SearchBar)
            is CustomCocktailSearchHeaderViewHolder -> holder.bind()
            is CustomCocktailSearchIngredientResultViewHolder -> holder.bind(items[position] as CustomCocktailSearchItem.IngredientResult)
        }
    }

    // 어댑터의 아이템 리스트 갱신
    fun updateItems(newItems: List<CustomCocktailSearchItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    // search bar
    inner class CustomCocktailSearchBarViewHolder(private val binding: ListItemCustomCocktailSearchBarBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CustomCocktailSearchItem.SearchBar) {

            binding.apply {

                // 취소 이벤트
                textViewListItemCustomCocktailSearchBarCancel.setOnClickListener {
                    customCocktailSearchListener.onClickCancel()
                }

                editTextCustomCocktailSearchBar.requestFocus()

                val imm = root.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

                editTextCustomCocktailSearchBar.postDelayed({
                    imm.showSoftInput(editTextCustomCocktailSearchBar, InputMethodManager.SHOW_IMPLICIT)
                }, 100)

                // 현재 검색어 설정
                editTextCustomCocktailSearchBar.setText(item.query ?: "")

                // 엔터를 눌렀을 때 액션 처리.
                editTextCustomCocktailSearchBar.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->

                    if(actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {

                        // 사용자가 입력한 검색어 가져오기.
                        val query = editTextCustomCocktailSearchBar.text.toString()

                        Log.d(TAG, "bind: OnEditorActionListener : $query")

                        // 검색어 전달 콜백 호출.
                        onSearchQueryChanged?.invoke(query)

                        // 키보드 닫기.
                        val closeKeyboard = root.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        closeKeyboard.hideSoftInputFromWindow(editTextCustomCocktailSearchBar.windowToken, 0)

                        return@OnEditorActionListener true
                    }
                    false
                })
            }
        }
    }

    // header
    inner class CustomCocktailSearchHeaderViewHolder(private val binding: ListItemCustomCocktailSearchHeaderBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind() {

            binding.apply {

            }
        }
    }

    // Ingredient Result
    inner class CustomCocktailSearchIngredientResultViewHolder(private val binding: ListItemCustomCocktailSearchResultBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CustomCocktailSearchItem.IngredientResult) {

            binding.apply {

                Glide.with(root.context)
                    .load(item.ingredientImage)
                    .placeholder(R.drawable.logo_final)
                    .error(R.drawable.logo_final)
                    .into(imageViewListItemCustomCocktailSearchResultIngredient)

                textViewListItemCustomCocktailSearchResultIngredientName.text = item.ingredientName

                root.setOnClickListener {
                    customCocktailSearchListener.onClickIngredient(item.ingredientId)
                }
            }
        }
    }
}