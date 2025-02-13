package com.hontail.ui.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.hontail.R
import com.hontail.data.model.response.CocktailTopLikedResponseItem
import com.hontail.databinding.ListItemHomeCategoryBinding
import com.hontail.databinding.ListItemHomePictureDescriptionBinding
import com.hontail.databinding.ListItemHomeToptenBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.home.screen.HomeCategoryItem
import com.hontail.ui.home.screen.HomeItem
import com.hontail.util.CommonUtils

class HomeAdapter(private val context: Context, private var items: MutableList<HomeItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mainActivity = context as MainActivity

    lateinit var homeListener: ItemOnClickListener

    interface ItemOnClickListener{
        fun onClickCategory(name: String)
        fun onClickTopTen(cocktailId: Int)
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is HomeItem.PictureDescription -> VIEW_TYPE_PICTURE_DESCRIPTION
            is HomeItem.Category -> VIEW_TYPE_CATEGORY
            is HomeItem.TopTen -> VIEW_TYPE_TOPTEN
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_PICTURE_DESCRIPTION -> {
                val binding = ListItemHomePictureDescriptionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                HomePictureDescriptionViewHolder(binding)
            }

            VIEW_TYPE_CATEGORY -> {
                val binding = ListItemHomeCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                HomeCategoryViewHolder(binding)
            }

            VIEW_TYPE_TOPTEN -> {
                val binding = ListItemHomeToptenBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                HomeTopTenViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is HomeItem.PictureDescription -> (holder as HomePictureDescriptionViewHolder).bind()
            is HomeItem.Category -> (holder as HomeCategoryViewHolder).bind(item.categoryList)
            is HomeItem.TopTen -> (holder as HomeTopTenViewHolder).bind(item.topTenList)
        }
    }


    inner class HomePictureDescriptionViewHolder(private val binding: ListItemHomePictureDescriptionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.textViewHomePictureBig.text = CommonUtils.changeTextColor(
                binding.root.context,
                "지금 당신을 위한\n완벽한 레시피를\n추천해드릴게요!",
                "완벽한 레시피",
                R.color.basic_pink
            )

            binding.imageViewHomePicture.setOnClickListener {
                mainActivity.changeFragment(
                    CommonUtils.MainFragmentName.COCKTAIL_TAKE_PICTURE_FRAGMENT
                )
            }
        }
    }

    inner class HomeCategoryViewHolder(private val binding: ListItemHomeCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(categoryList: List<HomeCategoryItem>) {
            val adapter = BaseCategoryAdapter(binding.root.context, categoryList)
            binding.recyclerViewHomeCategory.layoutManager = GridLayoutManager(binding.root.context, 3)
            binding.recyclerViewHomeCategory.adapter = adapter

            adapter.cocktailBaseCategoryListener = object : BaseCategoryAdapter.ItemOnClickListener{
                override fun onClickCategory(name: String) {
                    homeListener.onClickCategory(name)
                }

            }
        }
    }

    inner class HomeTopTenViewHolder(private val binding: ListItemHomeToptenBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(topTenList: List<CocktailTopLikedResponseItem>) {
            val adapter = TopTenAdapter(binding.root.context, topTenList)
            binding.recyclerViewHomeTopTen.layoutManager = SlowScrollLinearLayoutManager(binding.root.context)
            binding.recyclerViewHomeTopTen.adapter = adapter
            binding.recyclerViewHomeTopTen.addOnScrollListener(CenterScrollListener())

            val startPosition = topTenList.size * 100 // 리스트의 중앙으로 설정
            binding.recyclerViewHomeTopTen.scrollToPosition(startPosition)

            val pagerSnapHelper = PagerSnapHelper()
            pagerSnapHelper.attachToRecyclerView(binding.recyclerViewHomeTopTen)

            adapter.topTenListener = object : TopTenAdapter.ItemOnClickListener{
                override fun onClickTopTen(cocktailId: Int) {
                    homeListener.onClickTopTen(cocktailId)
                }

            }
        }
    }

    fun updateItems(newItems: List<HomeItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged() // 전체 갱신
    }

    companion object {
        const val VIEW_TYPE_PICTURE_DESCRIPTION = 0
        const val VIEW_TYPE_CATEGORY = 1
        const val VIEW_TYPE_TOPTEN = 2
    }
}
