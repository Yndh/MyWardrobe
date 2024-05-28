package com.example.mywardrobe.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.mywardrobe.R

class SelectCategoriesAdapter(
    private val context: Context,
    private val categories: Map<String, List<String>>,
    private val selectedCategories: MutableMap<String, MutableList<String>>,
    private val selectedCategoriesType: String?
) : BaseAdapter() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int = categories.size

    override fun getItem(position: Int): Map.Entry<String, List<String>> = categories.entries.elementAt(position)

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: inflater.inflate(R.layout.grid_item_category, parent, false)
        val categoryName = getItem(position).key
        val categoryType = getItem(position).value.firstOrNull() as String
        val categoryImage: ImageView = view.findViewById(R.id.categoryImage)
        val categoryTextView: TextView = view.findViewById(R.id.categoryTextView)

        categoryTextView.text = categoryName
        categoryImage.setBackgroundResource(
            if (selectedCategories.containsKey(categoryName)){
                R.drawable.category_selector_checked
            }
            else{
                R.drawable.category_selector
            }
        )

        view.setOnClickListener {
            if(selectedCategoriesType == null || selectedCategoriesType == categoryType){
                selectedCategories.clear()
                selectedCategories[categoryName] = mutableListOf(categoryType)
                notifyDataSetChanged()
            }else{
                Toast.makeText(context, "You can only select category of same types", Toast.LENGTH_SHORT).show()
            }

        }

        return view
    }
}
