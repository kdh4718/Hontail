import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
//import com.google.android.flexbox.FlexWrap
//import com.google.android.flexbox.FlexboxLayoutManager
//import com.google.android.flexbox.JustifyContent
import com.hontail.R
import com.hontail.databinding.ListItemPictureTopBinding
import com.hontail.ui.picture.PictureResultItem
import com.hontail.ui.picture.PictureTextAdapter
import com.hontail.util.CommonUtils

class PictureTopAdapter : RecyclerView.Adapter<PictureTopAdapter.TopViewHolder>() {
    private var item: PictureResultItem.TopItem? = null
    private lateinit var context: Context

    inner class TopViewHolder(private val binding: ListItemPictureTopBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PictureResultItem.TopItem) {
            binding.apply {
                textViewPictureResultSuggestion.text = CommonUtils.changeTextColor(
                    context,
                    item.suggestion,
                    "hyunn",
                    R.color.basic_sky
                )

                // Set up the ingredient RecyclerView
                recyclerViewPictureResultIngredient.apply {
//                    layoutManager = FlexboxLayoutManager(context).apply {
//                        flexWrap = FlexWrap.WRAP
//                        justifyContent = JustifyContent.FLEX_START
//                    }

                    val dataList = listOf(
                        "Salt", "Mint", "Sugar", "Lime", "Ice",
                        "Rum", "Soda", "Basil", "Peach", "Cherry",
                        "Lemon", "Orange"
                    )
                    adapter = PictureTextAdapter(context, dataList)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopViewHolder {
        context = parent.context
        return TopViewHolder(
            ListItemPictureTopBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TopViewHolder, position: Int) {
        item?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int = if (item != null) 1 else 0

    fun setItem(newItem: PictureResultItem.TopItem) {
        item = newItem
        notifyDataSetChanged()
    }
}