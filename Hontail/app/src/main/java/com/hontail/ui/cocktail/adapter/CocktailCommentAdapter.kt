package com.hontail.ui.cocktail.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hontail.R
import com.hontail.data.model.response.Comment
import com.hontail.databinding.ListItemCocktailCommentBinding

class CocktailCommentAdapter(private val context: Context, private var commentList: MutableList<Comment>, private val userId: Int?) : RecyclerView.Adapter<CocktailCommentAdapter.CocktailCommentHolder>() {

    lateinit var cocktailCommentListener: ItemOnClickListener

    interface ItemOnClickListener {
        fun onClickDelete(commentId: Int)
        fun onClickModify(commentId: Int, content: String)
    }

    private val swipedItems = mutableSetOf<Int>() // 스와이프된 아이템 저장

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CocktailCommentHolder {
        val binding = ListItemCocktailCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CocktailCommentHolder(binding)
    }

    override fun getItemCount(): Int = commentList.size

    override fun onBindViewHolder(holder: CocktailCommentHolder, position: Int) {
        holder.bindInfo(commentList[position])
    }

    fun updateComments(newComments: List<Comment>) {
        val diffCallback = object : DiffUtil.Callback() {
            override fun getOldListSize() = commentList.size

            override fun getNewListSize() = newComments.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return commentList[oldItemPosition].commentId == newComments[newItemPosition].commentId
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return commentList[oldItemPosition] == newComments[newItemPosition]
            }
        }

        val diffResult = DiffUtil.calculateDiff(diffCallback)
        commentList = newComments.toMutableList()
        diffResult.dispatchUpdatesTo(this)
    }

    inner class CocktailCommentHolder(private val binding: ListItemCocktailCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindInfo(item: Comment) {
            binding.apply {

                Glide.with(context)
                    .load(item.userImageUrl)
                    .placeholder(R.drawable.logo_image)
                    .into(imageViewCocktailCommentUserImage)

                textViewCocktailCommentUserName.text = item.userNickname
                textViewCocktailCommentUserComment.text = item.content


                // 현재 로그인 한 사용자 id와 같은 댓글 사용자 id에서만 나타나도록 '>'
                imageViewCocktailCommentUserComment.visibility = View.GONE
                if(item.userId == userId) {
                    imageViewCocktailCommentUserComment.visibility = View.VISIBLE
                }

                // 초기 위치 설정
                itemView.translationX = if (swipedItems.contains(adapterPosition)) -200f else 0f

                // 버튼 초기 회전 상태 설정
                imageViewCocktailCommentUserComment.rotation = if (swipedItems.contains(adapterPosition)) 180f else 0f

                // 옵션 버튼 클릭 시 스와이프 or 원상 복구
                imageViewCocktailCommentUserComment.setOnClickListener {
                    if (swipedItems.contains(adapterPosition)) {
                        resetSwipe()
                    } else {
                        swipeItem()
                    }
                }


            }
        }

        // 아이템을 왼쪽으로 스와이프 + 버튼 회전
        private fun swipeItem() {
            itemView.animate().translationX(-200f).setDuration(300).start() // 200px 왼쪽으로 이동
            binding.imageViewCocktailCommentUserComment.animate().rotation(180f).setDuration(300).start() // 버튼 180도 회전
            swipedItems.add(adapterPosition)
        }

        // 아이템을 원래 위치로 복구 + 버튼 원래 위치로 회전
        private fun resetSwipe() {
            itemView.animate().translationX(0f).setDuration(300).start() // 원래 위치로 복귀
            binding.imageViewCocktailCommentUserComment.animate().rotation(0f).setDuration(300).start() // 버튼 원래 방향으로 복귀
            swipedItems.remove(adapterPosition)
        }
    }
}
