package com.example.mywardrobe.adapters

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mywardrobe.R
import com.example.mywardrobe.managers.ClothingItem
import com.example.mywardrobe.managers.ClothingItemsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ClothingItemAdapter(
    private val context: Context,
    private var clothingItems: List<ClothingItem>,
    private val onItemClick: (ClothingItem) -> Unit
) : RecyclerView.Adapter<ClothingItemAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(clothingItems[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.grid_item_clothing, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = clothingItems[position]

        CoroutineScope(Dispatchers.Main)
            .launch {
                Glide.with(context)
                    .load(ClothingItemsManager.getImage(context, item.imageName))
                    .into(holder.imageView)
            }    }

    override fun getItemCount(): Int = clothingItems.size

    fun updateItems(newItems: List<ClothingItem>) {
        clothingItems = newItems
        notifyDataSetChanged()
    }
}
