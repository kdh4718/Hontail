package com.hontail.ui.mypage.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hontail.data.model.response.CocktailListResponse
import com.hontail.databinding.ListItemMypageCocktailBinding
import com.hontail.databinding.ListItemMypageEmptyBinding
import com.hontail.databinding.ListItemMypageProfileBinding
import com.hontail.ui.mypage.screen.MyPageItem
import com.hontail.util.CocktailItemAdapter

private const val TAG = "MyPageAdapter"
class MyPageAdapter(private val context: Context, var items: List<MyPageItem>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var myPageListener: ItemOnClickListener

    interface ItemOnClickListener {
        fun onClickToCocktailDetail(cocktailId: Int)
        fun onClickManageProfile()
        fun onClickRequestIngredient()
    }

    companion object {
        const val VIEW_TYPE_PROFILE = 0
        const val VIEW_TYPE_COCKTAIL = 1
        const val VIEW_TYPE_EMPTY = 2
    }

    override fun getItemViewType(position: Int): Int {

        val item = items[position]

        Log.d(TAG, "getItemViewType: position $position -> $item")

        return when(item) {
            is MyPageItem.Profile -> VIEW_TYPE_PROFILE
            is MyPageItem.MyCocktail -> VIEW_TYPE_COCKTAIL
            is MyPageItem.Empty -> VIEW_TYPE_EMPTY
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {

            VIEW_TYPE_PROFILE -> {
                val binding = ListItemMypageProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                MyPageProfileViewHolder(binding)
            }

            VIEW_TYPE_COCKTAIL -> {
                val binding = ListItemMypageCocktailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                MyPageCocktailViewHolder(binding)
            }

            VIEW_TYPE_EMPTY -> {
                val binding = ListItemMypageEmptyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                MyPageEmptyViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Unknown ViewType: $viewType")
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when(val item = items[position]) {
            is MyPageItem.Profile -> (holder as MyPageProfileViewHolder).bind(item)
            is MyPageItem.MyCocktail -> (holder as MyPageCocktailViewHolder).bind(item.cocktailList)
            is MyPageItem.Empty -> (holder as MyPageEmptyViewHolder).bind()
        }
    }

    fun updateItems(newItems: List<MyPageItem>) {
        Log.d(TAG, "updateItems: 새로운 아이템 갱신 -> $newItems")
        items = newItems
        notifyDataSetChanged()
    }

    // 프로필 ViewHolder
    inner class MyPageProfileViewHolder(private val binding: ListItemMypageProfileBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MyPageItem.Profile) {
            Log.d(TAG, "bind: MyPageProfileViewHolder bind() 호출됌. -> $item")

            binding.apply {

                Glide.with(context)
                    .load(item.userInfo.user_image_url)
                    .into(imageViewMyPageProfile)

                textViewMyPageNickname.text = item.userInfo.user_nickname
                textViewMyPageCocktailCount.text = "레시피 ${item.cocktailCnt}개"

                // 프로필 관리
                buttonProfileManagement.setOnClickListener {
                    myPageListener.onClickManageProfile()
                }

                // 재료 요청
                buttonRequestMaterial.setOnClickListener {
                    myPageListener.onClickRequestIngredient()
                }
            }
        }
    }

    // 나만의 칵테일 ViewHolder
    inner class MyPageCocktailViewHolder(private val binding: ListItemMypageCocktailBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(cocktailList: List<CocktailListResponse>) {

            binding.apply {

                val myPageCocktailAdapter = CocktailItemAdapter(context, cocktailList)

                recyclerViewListItemMyPageCocktail.layoutManager = GridLayoutManager(context, 2)
                recyclerViewListItemMyPageCocktail.adapter = myPageCocktailAdapter

                myPageCocktailAdapter.cocktailItemListener = object : CocktailItemAdapter.ItemOnClickListener {
                    override fun onClickCocktailItem(cocktailId: Int) {
                        myPageListener.onClickToCocktailDetail(cocktailId)
                    }
                }

            }
        }
    }

    // 비어있을 때 ViewHolder
    inner class MyPageEmptyViewHolder(private val binding: ListItemMypageEmptyBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind() {

            binding.apply {
                textViewListItemMyPageEmptyTitle.text = "나만의 칵테일이 없어요."
                textViewListItemMyPageEmptySubTitle.text = "hyuun님만의 레시피를 등록해주세요."
            }
        }
    }

}