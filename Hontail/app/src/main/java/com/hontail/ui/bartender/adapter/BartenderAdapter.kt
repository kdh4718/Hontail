package com.hontail.ui.bartender.adapter

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hontail.databinding.ListItemChatLeftBinding
import com.hontail.databinding.ListItemChatLeftCocktailBinding
import com.hontail.databinding.ListItemChatRightBinding
import com.hontail.ui.bartender.screen.ChatMessage

class BartenderAdapter(private val context: Context, private var messages: List<ChatMessage>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var bartenderListener: ItemOnClickListener

    interface ItemOnClickListener {
        fun onClickCocktailImage(cocktailId: Int)
    }

    companion object {
        private const val VIEW_TYPE_USER = 1
        private const val VIEW_TYPE_BARTENDER = 2
        private const val VIEW_TYPE_COCKTAIL = 3
    }

    // User인지 Bartender인지 구분.
    override fun getItemViewType(position: Int): Int {
        val message = messages[position]

        return when {
            message.cocktail != null -> VIEW_TYPE_COCKTAIL
            message.isUser -> VIEW_TYPE_USER
            else -> VIEW_TYPE_BARTENDER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when(viewType) {

            VIEW_TYPE_USER -> {
                val binding = ListItemChatRightBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return UserMessageViewHolder(binding)
            }

            VIEW_TYPE_BARTENDER -> {
                val binding = ListItemChatLeftBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return BartenderMessageViewHolder(binding)
            }

            VIEW_TYPE_COCKTAIL -> {
                val binding = ListItemChatLeftCocktailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return CocktailMessageViewHolder((binding))
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        val isContinuous = isContinuousMessage(position)
        val isLastMessageInSameTime = isLastMessageInSameTime(position)

        when(holder) {

            is UserMessageViewHolder -> holder.bind(message, isContinuous, isLastMessageInSameTime)
            is BartenderMessageViewHolder -> holder.bind(message, isContinuous, isLastMessageInSameTime)
            is CocktailMessageViewHolder -> holder.bind(message)
        }
    }

    // 이전 메시지와 비교해서 같은 사람이 연속으로 보낸 메시지인지 확인.
    private fun isContinuousMessage(position: Int): Boolean {
        if(position == 0) return false

        val currentMessage = messages[position]
        val previousMessage = messages[position - 1]

        // 같은 사람이 보냈고, 시간이 같다면 연속 메시지로 판단.
        return currentMessage.isUser == previousMessage.isUser && currentMessage.timestamp == previousMessage.timestamp
    }

    // 같은 시간대의 마지막 메시지인지 확인
    private fun isLastMessageInSameTime(position: Int): Boolean {
        if(position == messages.size - 1) return true

        val currentMessage = messages[position]
        val nextMessage = messages[position + 1]

        // 다음 메시지가 같은 사람이고, 같은 시간대인지 확인
        return !(currentMessage.isUser == nextMessage.isUser && currentMessage.timestamp == nextMessage.timestamp)
    }

    // dp 값을 px로 변환
    private fun dpToPx(context: Context, dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            context.resources.displayMetrics
        ).toInt()
    }

    fun updateMessages(newMessages: List<ChatMessage>) {
        messages = newMessages
        notifyDataSetChanged()
    }

    // User 메시지
    inner class UserMessageViewHolder(private val binding: ListItemChatRightBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(message: ChatMessage, isContinuous: Boolean, isLastMessageInSameTime: Boolean) {
            binding.apply {
                textViewListItemChatRightMessage.text = message.message

                // 같은 시간대의 마지막 메시지인 경우에만 보이도록.
                textViewListItemChatRightTime.text = message.timestamp
                textViewListItemChatRightTime.visibility = if(isLastMessageInSameTime) View.VISIBLE else View.GONE

                // 같은 사용자가 보냈다면 12dp, 아니라면 32dp marginTop 주기.
                val params = root.layoutParams as MarginLayoutParams
                params.topMargin = dpToPx(root.context, if(isContinuous) 4 else 32)
                root.layoutParams = params
            }
        }
    }

    // Bartender 메시지
    inner class BartenderMessageViewHolder(private val binding: ListItemChatLeftBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(message: ChatMessage, isContinuous: Boolean, isLastMessageInSameTime: Boolean) {
            binding.apply {
                textViewListItemChatLeftMessage.text = message.message

                // 연속된 메시지라면 프로필과 이름 숨기기
                if(isContinuous) {
                    imageViewListItemChatLeftProfile.visibility = View.GONE
                    textViewListItemChatLeftName.visibility = View.GONE
                }
                else {
                    imageViewListItemChatLeftProfile.visibility = View.VISIBLE
                    textViewListItemChatLeftName.visibility = View.VISIBLE
                }

                // 같은 시간대의 마지막 메시지인 경우에만 보이도록.
                textViewListItemChatLeftTime.text = message.timestamp
                textViewListItemChatLeftTime.visibility = if(isLastMessageInSameTime) View.VISIBLE else View.GONE

                // 같은 사용자가 보냈다면 12dp, 아니라면 32dp marginTop 주기.
                val params = root.layoutParams as MarginLayoutParams
                params.topMargin = dpToPx(root.context, if(isContinuous) 4 else 32)
                root.layoutParams = params
            }
        }
    }

    inner class CocktailMessageViewHolder(private val binding: ListItemChatLeftCocktailBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ChatMessage) {

            binding.apply {

                textViewListItemChatLeftCocktailName.text = "칵테일러 스위프트"

                Glide.with(context)
                    .load(item.cocktail?.imageUrl)
                    .into(imageViewListItemChatLeftCocktailCocktail)

                textViewListItemChatLeftCocktailMessage.text = item.message

                imageViewListItemChatLeftCocktailCocktail.setOnClickListener {
                    bartenderListener.onClickCocktailImage(item.cocktail!!.id)
                }
            }
        }
    }

}