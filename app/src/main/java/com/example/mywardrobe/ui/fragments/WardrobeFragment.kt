package com.example.mywardrobe.ui.fragments

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.GridView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.core.view.size
import com.example.mywardrobe.R
import com.example.mywardrobe.adapters.ClothingItemAdapter
import com.example.mywardrobe.managers.ClothingItem
import com.example.mywardrobe.managers.ClothingItemsManager
import com.example.mywardrobe.managers.ClothingTagsManager
import com.example.mywardrobe.managers.ClothingCategoriesManager
import com.example.mywardrobe.managers.Tag
import com.example.mywardrobe.ui.fragments.ClothingItemDetailsFragment
import com.example.mywardrobe.ui.fragments.NewClothingFragment
import com.example.mywardrobe.ui.fragments.NewClothingTagFragment
import com.google.gson.Gson

private const val PREF_IS_LINEAR = "is_linear"

class WardrobeFragment : Fragment() {
    private lateinit var wardrobeScrollView: ScrollView
    private lateinit var wardobeLinearLayout: LinearLayout
    private lateinit var categoriesLinearLayout: LinearLayout
    private lateinit var noPiecesTextView: TextView

    private val selectedCategories = mutableListOf<String>()
    private val selectedTags = mutableListOf<Int>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wardrobe, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        wardobeLinearLayout = view.findViewById(R.id.wardobeLinearLayout)
        wardrobeScrollView = view.findViewById(R.id.wardrobeScrollView)
        categoriesLinearLayout = view.findViewById(R.id.categoriesLinearLayout)
        noPiecesTextView = view.findViewById(R.id.noPiecesTextView)

        val clothingItems = ClothingItemsManager.getClothingItems()

        Log.d("WardobeFragment", "start")
        for(item in clothingItems){
            Log.d("WardobeFragment", "$item")
        }
        displayClothingItems(clothingItems)
        //val clothingTypes = ClothingCategoriesManager.getTypes()
        val categories = ClothingCategoriesManager.getCategories()
        displayCategories(categories)

    }


    fun displayCategories(categories: Map<String, List<String>>){
        var typeCheckboxIdCounter = 1
        for(category in categories){
            val checkbox = CheckBox(requireContext())
            val checkboxLayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT

            ).also { checkbox.layoutParams = it }
            checkboxLayoutParams.setMargins(0, 0, 20, 0)
            checkbox.setPadding(50, 30, 50, 30)
            checkbox.setTextColor(ContextCompat.getColor(requireContext(), R.color.checkboxAccent))
            checkbox.background = ContextCompat.getDrawable(requireContext(),
                R.drawable.radio_border
            )
            checkbox.buttonDrawable = null
            checkbox.text = category.key.toString()
            checkbox.textSize = 12f
            checkbox.id = typeCheckboxIdCounter++
            checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
                typeChecked(buttonView, isChecked)
            }

            categoriesLinearLayout.addView(checkbox)
        }

    }

    private fun typeChecked(buttonView: CompoundButton?, checked: Boolean) {
        if(checked){
            selectedCategories.add(buttonView?.text.toString())
            buttonView?.setTextColor(ContextCompat.getColor(requireContext(), R.color.fontSecondary))
        }else{
            selectedCategories.remove(buttonView?.text)
            buttonView?.setTextColor(ContextCompat.getColor(requireContext(), R.color.checkboxAccent))
        }
        filterClothingItems()
    }

    private fun tagChecked(checked: Boolean, id: Int) {
        if(checked){
            selectedTags.add(id)
        }else{
            selectedTags.remove(id)
        }
        //filterClothingItems()
    }

    private fun filterClothingItems() {
        val filteredItems = if (selectedCategories.isEmpty() && selectedTags.isEmpty()) {
            ClothingItemsManager.getClothingItems()
        } else {
            ClothingItemsManager.getClothingItems().filter { item ->
                val typeMatches = selectedCategories.isEmpty() || item.categories.any { selectedCategories.contains(it) }
                val tagMatches = selectedTags.isEmpty() || item.tags.any { it in selectedTags }
                typeMatches && tagMatches
            }
        }
        wardobeLinearLayout.removeAllViews()
        displayClothingItems(filteredItems)
    }




    fun displayClothingItems(clothingItems: List<ClothingItem>) {
        wardobeLinearLayout.removeAllViews()

        if (clothingItems.isEmpty()) {
            noPiecesTextView.visibility = View.VISIBLE
            return
        }

        noPiecesTextView.visibility = View.GONE

        val wardrobeGridView = GridView(requireContext())
        val wardrobeLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            (clothingItems.size/2)*1000
        ).also { wardrobeGridView.layoutParams = it }
        wardrobeGridView.numColumns = 2
        wardrobeLayoutParams.gravity = Gravity.CENTER
        wardrobeGridView.requestLayout()

        val adapter = ClothingItemAdapter(requireContext(), clothingItems)
        wardrobeGridView.adapter = adapter

        wardrobeGridView.setOnItemClickListener { parent, view, position, id ->
            val item = clothingItems[position]
            val bundle = Bundle().apply {
                putString("clothingItem", Gson().toJson(item))
            }
            val detailsFragment = ClothingItemDetailsFragment().apply {
                arguments = bundle
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentFrame, detailsFragment)
                .addToBackStack(null)
                .commit()
        }
        wardobeLinearLayout.addView(wardrobeGridView)
    }
}