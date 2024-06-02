import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.mywardrobe.R
import com.example.mywardrobe.managers.ClothingItem
import com.example.mywardrobe.managers.ClothingItemsManager
import com.example.mywardrobe.ui.fragments.OutfitClothingItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StylingItemsAdapter(
    private val context: Context,
    private val items: List<OutfitClothingItem>
) : RecyclerView.Adapter<StylingItemsAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val clothingItemImageView: ImageView = itemView.findViewById(R.id.clothingItemImageView)
        val pinImageView: ImageView = itemView.findViewById(R.id.pinImageView)
        val outfitItemLinearLayout: LinearLayout = itemView.findViewById(R.id.outfitItemLinearLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.clothing_item_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var itemList = items[position]
        val item = itemList.item
        val bitmap = ClothingItemsManager.getImage(context, item.imageName)
            holder.clothingItemImageView.setImageBitmap(bitmap)
            holder.clothingItemImageView.scaleType = ImageView.ScaleType.CENTER_INSIDE


        holder.pinImageView.setImageResource(
            if (itemList.isLocked) R.drawable.baseline_push_pin_24 else R.color.background
        )
        holder.pinImageView.background = ContextCompat.getDrawable(context,
            if(itemList.isLocked) R.drawable.secondary_backdrop_background
            else R.color.background
        )

        holder.outfitItemLinearLayout.setOnClickListener {
            holder.pinImageView.setImageResource(
                if(itemList.isLocked)  R.color.background
                else R.drawable.baseline_push_pin_24
            )
            holder.pinImageView.background = ContextCompat.getDrawable(context,
                if(itemList.isLocked) R.drawable.secondary_backdrop_background
                else R.color.background
                )
            itemList.isLocked = !itemList.isLocked
            notifyDataSetChanged()
        }

    }

    override fun getItemCount(): Int = items.size
}
