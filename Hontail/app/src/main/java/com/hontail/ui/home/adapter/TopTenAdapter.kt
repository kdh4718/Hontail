package com.hontail.ui.home.adapter

import android.content.Context
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hontail.R
import com.hontail.data.model.response.CocktailTopLikedResponseItem
import com.hontail.databinding.ListItemHomeToptenToptenBinding

class TopTenAdapter(val context: Context, var topTenList: List<CocktailTopLikedResponseItem>) : RecyclerView.Adapter<TopTenAdapter.TopTenHolder>() {

    lateinit var topTenListener: ItemOnClickListener

    interface ItemOnClickListener {
        fun onClickTopTen(cocktailId: Int)
    }

    inner class TopTenHolder(private val binding: ListItemHomeToptenToptenBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item : CocktailTopLikedResponseItem){
            binding.apply {
                textViewHomeTopTenNumber.text = item.rank.toString()
                textViewHomeTopTenName.text = item.cocktailName

                Glide.with(context)
                    .load(item.imageUrl)
                    .placeholder(R.drawable.ic_bottom_navi_zzim_selected)
                    .error(R.drawable.ic_bottom_navi_zzim_unselected)
                    .into(imageViewHomeTopTen)

                root.setOnClickListener{
                    topTenListener.onClickTopTen(item.id)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopTenHolder {
        val binding = ListItemHomeToptenToptenBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TopTenHolder(binding)
    }

    override fun getItemCount(): Int {
        return Integer.MAX_VALUE
    }

    override fun onBindViewHolder(holder: TopTenHolder, position: Int) {
        val actualPosition = position % topTenList.size
        holder.bind(topTenList[actualPosition])
    }
}

class CenterScrollListener : RecyclerView.OnScrollListener() {
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val center = recyclerView.width / 2  // RecyclerView의 중앙 위치
        val childCount = recyclerView.childCount

        for (i in 0 until childCount) {
            val child = recyclerView.getChildAt(i) ?: continue
            val childCenter = (child.left + child.right) / 2
            val distance = Math.abs(center - childCenter)

            // 거리 기반으로 스케일 조정
            val scale = 1f - (distance / recyclerView.width.toFloat())
            child.scaleX = 0.6f + scale * 0.4f  // 기본 0.6, 최대 1.0
            child.scaleY = 0.6f + scale * 0.4f
        }
    }
}

class SlowScrollLinearLayoutManager(context: Context) : LinearLayoutManager(context, HORIZONTAL, false) {

    override fun smoothScrollToPosition(
        recyclerView: RecyclerView,
        state: RecyclerView.State,
        position: Int
    ) {
        val smoothScroller = object : LinearSmoothScroller(recyclerView.context) {
            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                val speedPerPixel = 0.1f / displayMetrics.densityDpi
                return speedPerPixel
            }
        }
        smoothScroller.targetPosition = position
        startSmoothScroll(smoothScroller)
    }
}