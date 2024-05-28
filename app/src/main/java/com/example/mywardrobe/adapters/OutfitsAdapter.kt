package com.example.mywardrobe.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.mywardrobe.R
import com.example.mywardrobe.managers.ClothingItem
import com.example.mywardrobe.managers.ClothingItemsManager
import com.example.mywardrobe.managers.Outfit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OutfitsAdapter(
    private val context: Context,
    private var outfits: List<Outfit>,
    private val onItemClick: (Outfit) -> Unit
) : RecyclerView.Adapter<OutfitsAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageContainer: LinearLayout = view.findViewById(R.id.imageContainer)

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION){
                    onItemClick(outfits[position])
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.grid_item_outfit, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imageContainer.removeAllViews()

        val outfit = outfits[position]
        outfit.items.forEach { clothingItem ->
            val imageView = ImageView(context)
            imageView.layoutParams = LinearLayout.LayoutParams(
                200,
                200
            )

            CoroutineScope(Dispatchers.Main).launch {
                val bitmap = withContext(Dispatchers.IO) {
                    ClothingItemsManager.getImage(context, clothingItem.imageName)
                }
                imageView.setImageBitmap(bitmap)
                imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE
            }
            holder.imageContainer.addView(imageView)
        }
    }

    override fun getItemCount(): Int = outfits.size

    fun updateItems(newOutfits: List<Outfit>) {
        outfits = newOutfits
        notifyDataSetChanged()
    }
}
