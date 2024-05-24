package com.example.mywardrobe.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.mywardrobe.R
import com.example.mywardrobe.managers.ClothingItem
import com.example.mywardrobe.managers.ClothingItemsManager

class ClothingItemAdapter(
    private val context: Context,
    private val clothingItems: List<ClothingItem>
): BaseAdapter() {
    override fun getCount(): Int {
        return clothingItems.size
    }

    override fun getItem(position: Int): Any {
        return clothingItems[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val gridView: View
        if (convertView == null) {
            gridView = inflater.inflate(R.layout.grid_item_clothing, null)
            val imageView = gridView.findViewById<ImageView>(R.id.imageView)
            val item = clothingItems[position]
            imageView.setImageBitmap(ClothingItemsManager.getImage(context, item.imageName))
        } else {
            gridView = convertView
        }
        return gridView
    }
}