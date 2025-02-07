import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hontail.databinding.ListItemPictureBottomBinding
import com.hontail.databinding.ListItemCocktailBinding
import com.hontail.ui.picture.PictureResultItem

class PictureBottomAdapter : RecyclerView.Adapter<PictureBottomAdapter.BottomViewHolder>() {
    private var item: PictureResultItem.BottomItem? = null
    private lateinit var context: Context

    inner class BottomViewHolder(private val binding: ListItemPictureBottomBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PictureResultItem.BottomItem) {
            binding.apply {
                textViewPictureResultCocktailList.text = item.cocktailCount

                // Set up the cocktail RecyclerView with GridLayoutManager
                recyclerViewPictureResultCocktailList.apply {
                    layoutManager = GridLayoutManager(context, 2).apply {
                        spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                            override fun getSpanSize(position: Int): Int = 1
                        }
                    }

                    // Remove any existing item decorations
                    if (itemDecorationCount > 0) {
                        removeItemDecorationAt(0)
                    }

                    // Add item decoration for spacing
                    addItemDecoration(object : RecyclerView.ItemDecoration() {
                        override fun getItemOffsets(
                            outRect: android.graphics.Rect,
                            view: android.view.View,
                            parent: RecyclerView,
                            state: RecyclerView.State
                        ) {
                            val position = parent.getChildAdapterPosition(view)
                            val column = position % 2

                            // Set horizontal spacing
                            outRect.left = if (column == 0) 0 else 20 // 20dp for right item
                            outRect.right = if (column == 0) 20 else 0 // 20dp for left item

                            // Set vertical spacing - 32dp between rows
                            if (position >= 2) {
                                outRect.top = 32
                            }
                        }
                    })

                    // Sample data for cocktails (10 items for 5 rows)
                    val cocktailList = List(10) {
                        CocktailItem(
                            name = "깔루아 밀크",
                            baseSpirit = "리큐어",
                            ingredientCount = "재료 2개",
                            alcoholContent = "5%",
                            zzimCount = "1,231"
                        )
                    }

                    adapter = CocktailListAdapter(cocktailList)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BottomViewHolder {
        context = parent.context
        return BottomViewHolder(
            ListItemPictureBottomBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: BottomViewHolder, position: Int) {
        item?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int = if (item != null) 1 else 0

    fun setItem(newItem: PictureResultItem.BottomItem) {
        item = newItem
        notifyDataSetChanged()
    }
}

// Data class for cocktail items
data class CocktailItem(
    val name: String,
    val baseSpirit: String,
    val ingredientCount: String,
    val alcoholContent: String,
    val zzimCount: String
)

// Adapter for cocktail items
class CocktailListAdapter(private val cocktails: List<CocktailItem>) :
    RecyclerView.Adapter<CocktailListAdapter.CocktailViewHolder>() {

    inner class CocktailViewHolder(private val binding: ListItemCocktailBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CocktailItem) {
            binding.apply {
                textViewListItemCocktailName.text = item.name
                textViewListItemCocktailBaseSpirit.text = item.baseSpirit
                textViewListItemCocktailIngredientCount.text = item.ingredientCount
                textViewListItemCocktailAlcoholContent.text = item.alcoholContent
                textViewListItemCocktailTotalZzim.text = item.zzimCount
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CocktailViewHolder {
        return CocktailViewHolder(
            ListItemCocktailBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CocktailViewHolder, position: Int) {
        holder.bind(cocktails[position])
    }

    override fun getItemCount(): Int = cocktails.size
}