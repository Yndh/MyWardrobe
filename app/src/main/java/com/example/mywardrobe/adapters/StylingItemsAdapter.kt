package com.example.mywardrobe.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mywardrobe.R
import com.example.mywardrobe.managers.ClothingItem
import com.example.mywardrobe.managers.ClothingItemsManager

class StylingItemsAdapter(
    private val context: Context,
    private val items: List<ClothingItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_TRANSPARENT = 0
        private const val VIEW_TYPE_ITEM = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_TRANSPARENT -> BackdropViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.empty_background, parent, false))
            VIEW_TYPE_ITEM -> ClothingItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.clothing_item_view, parent, false))
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            VIEW_TYPE_TRANSPARENT -> {

            }
            VIEW_TYPE_ITEM -> {
                val item = items[position - 1]
                val itemHolder = holder as ClothingItemViewHolder
                Glide.with(context)
                    .load(ClothingItemsManager.getImage(context, item.imageName))
                    .into(itemHolder.imageView)
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size + 2
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position == 0 || position == itemCount - 1 -> VIEW_TYPE_TRANSPARENT
            else -> VIEW_TYPE_ITEM
        }
    }

    fun getItems(): List<ClothingItem>{
        return  items
    }

    inner class BackdropViewHolder(view: View) : RecyclerView.ViewHolder(view)

    inner class ClothingItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.clothingItemImageView)
    }
}
