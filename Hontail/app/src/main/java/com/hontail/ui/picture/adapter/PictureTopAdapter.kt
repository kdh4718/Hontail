//PictureTopAdapter.kt
package com.hontail.ui.picture.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.hontail.R
import com.hontail.databinding.ListItemPictureTopBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.picture.screen.CocktailPictureResultFragment
import com.hontail.util.CommonUtils

class PictureTopAdapter(
    private val context: Context,
    private var data: CocktailPictureResultFragment.PictureResultType.Top
) : RecyclerView.Adapter<PictureTopAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ListItemPictureTopBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CocktailPictureResultFragment.PictureResultType.Top) {
            binding.textViewPictureResultSuggestion.text =
                CommonUtils.changeTextColor(context, item.suggestion, "hyunn", R.color.basic_sky)

            val layoutManager = FlexboxLayoutManager(context).apply {
                flexWrap = FlexWrap.WRAP
                justifyContent = JustifyContent.FLEX_START
            }
            binding.recyclerViewPictureResultIngredient.layoutManager = layoutManager
            binding.recyclerViewPictureResultIngredient.adapter =
                PictureTextAdapter(context, item.ingredients)

            binding.imageViewPictureResultAdd.setOnClickListener {
                CommonUtils.showDialog(
                    context,
                    "혹시 찍은 재료가 없나요?",
                    "없다면 재료를 등록해보세요!"
                ) {
                    (context as MainActivity).changeFragment(
                        CommonUtils.MainFragmentName.INGREDIENT_ADD_FRAGMENT
                    )
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListItemPictureTopBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data)
    }

    override fun getItemCount(): Int = 1
}