package com.example.mywardrobe.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.mywardrobe.R
import com.example.mywardrobe.managers.Tag

class SelectTagsAdapter(
    private val context: Context,
    private val types: List<Tag>,
    private val selectedTypes: MutableList<Tag>
): BaseAdapter() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int = types.size

    override fun getItem(position: Int): Tag = types[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
       val view = convertView ?: inflater.inflate(R.layout.grid_item_category, parent, false)
        val tag = getItem(position)
        val tagImage: ImageView = view.findViewById(R.id.categoryImage)
        val tagTextView: TextView = view.findViewById(R.id.categoryTextView)

        tagTextView.text = tag.name
        tagImage.setBackgroundResource(
            if (selectedTypes.contains(tag)){
                R.drawable.category_selector_checked
            }else{
                R.drawable.category_selector
            }
        )

        view.setOnClickListener {
            selectedTypes.remove(tag)
            notifyDataSetChanged()
        }

        return view
    }
}