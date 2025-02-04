package com.hontail.ui.cocktail

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hontail.databinding.ListItemCocktailCommentBinding

class CocktailCommentAdapter(val context: Context, var commentList: List<Comment>) : RecyclerView.Adapter<CocktailCommentAdapter.CocktailCommentHolder>() {

    inner class CocktailCommentHolder(val binding: ListItemCocktailCommentBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindInfo(item: Comment) {
            binding.apply {
                imageViewCocktailCommentUserImage.setImageResource(item.imageRes)
                textViewCocktailCommentUserName.text = item.name
                textViewCocktailCommentUserComment.text = item.comment
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CocktailCommentHolder {
        val binding = ListItemCocktailCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CocktailCommentHolder(binding)
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

    override fun onBindViewHolder(holder: CocktailCommentHolder, position: Int) {
        holder.bindInfo(commentList[position])
    }
}
