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

    private val swipedItems = mutableSetOf<Int>()
    private val SWIPE_AMOUNT = -350f

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

    inner class CocktailCommentHolder(private val binding: ListItemCocktailCommentBinding) : RecyclerView.ViewHolder(binding.root) {

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

                // 초기 상태 설정
                contentContainer.translationX = if (swipedItems.contains(adapterPosition)) SWIPE_AMOUNT else 0f
                imageViewCocktailCommentUserComment.rotation = if (swipedItems.contains(adapterPosition)) 180f else 0f

                // 화살표 버튼 클릭 이벤트
                imageViewCocktailCommentUserComment.setOnClickListener {
                    if (swipedItems.contains(adapterPosition)) {
                        resetSwipe()
                    } else {
                        swipeItem()
                    }
                }

                // 수정 모드
                imageViewEdit.setOnClickListener {
                    cocktailCommentListener.onClickModify(item.commentId, item.content)
                }

                // 삭제
                imageViewDelete.setOnClickListener {
                    cocktailCommentListener.onClickDelete(item.commentId)
                }
            }
        }

        private fun swipeItem() {
            binding.apply {
                // 컨텐츠 스와이프 애니메이션
                contentContainer.animate()
                    .translationX(SWIPE_AMOUNT)
                    .setDuration(300)
                    .start()

                // 화살표 회전 애니메이션
                imageViewCocktailCommentUserComment.animate()
                    .rotation(180f)
                    .setDuration(300)
                    .start()
            }
            swipedItems.add(adapterPosition)
        }

        private fun resetSwipe() {
            binding.apply {
                // 컨텐츠 원위치 애니메이션
                contentContainer.animate()
                    .translationX(0f)
                    .setDuration(300)
                    .start()

                // 화살표 원위치 회전
                imageViewCocktailCommentUserComment.animate()
                    .rotation(0f)
                    .setDuration(300)
                    .start()
            }
            swipedItems.remove(adapterPosition)
        }
    }
}
