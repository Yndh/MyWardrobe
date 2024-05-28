package com.example.mywardrobe.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mywardrobe.R
import com.example.mywardrobe.adapters.ClothingItemAdapter
import com.example.mywardrobe.managers.ClothingItem
import com.example.mywardrobe.managers.ClothingItemsManager
import com.example.mywardrobe.managers.ClothingCategoriesManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WardrobeFragment : Fragment() {
    private lateinit var categoriesLinearLayout: LinearLayout
    private lateinit var noPiecesTextView: TextView
    private lateinit var wardrobeRecyclerView: RecyclerView
    private lateinit var clothingItemAdapter: ClothingItemAdapter

    private val selectedCategories = mutableListOf<String>()
    private val selectedTags = mutableListOf<Int>()

    private var bottomSheetDialog: BottomSheetDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_wardrobe, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        categoriesLinearLayout = view.findViewById(R.id.categoriesLinearLayout)
        noPiecesTextView = view.findViewById(R.id.noPiecesTextView)
        wardrobeRecyclerView = view.findViewById(R.id.wardrobeRecyclerView)

        wardrobeRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        clothingItemAdapter = ClothingItemAdapter(requireContext(), listOf()) { item -> displayDetails(item) }
        wardrobeRecyclerView.adapter = clothingItemAdapter

        CoroutineScope(Dispatchers.Main).launch {
            val clothingItems = withContext(Dispatchers.IO) { ClothingItemsManager.getClothingItems() }
            displayClothingItems(clothingItems)
            val categories = withContext(Dispatchers.IO) { ClothingCategoriesManager.getCategories() }
            displayCategories(categories)
        }
    }

    private fun displayCategories(categories: Map<String, List<String>>) {
        var typeCheckboxIdCounter = 1
        for (category in categories) {
            val checkbox = CheckBox(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(0, 0, 20, 0) }
                setPadding(50, 30, 50, 30)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.checkboxAccent))
                background = ContextCompat.getDrawable(requireContext(), R.drawable.radio_border)
                buttonDrawable = null
                text = category.key
                textSize = 12f
                id = typeCheckboxIdCounter++
                setOnCheckedChangeListener { buttonView, isChecked ->
                    typeChecked(buttonView, isChecked)
                }
            }
            categoriesLinearLayout.addView(checkbox)
        }
    }

    private fun typeChecked(buttonView: CompoundButton?, checked: Boolean) {
        if (checked) {
            selectedCategories.add(buttonView?.text.toString())
            buttonView?.setTextColor(ContextCompat.getColor(requireContext(), R.color.fontSecondary))
        } else {
            selectedCategories.remove(buttonView?.text)
            buttonView?.setTextColor(ContextCompat.getColor(requireContext(), R.color.checkboxAccent))
        }
        filterClothingItems()
    }

    private fun filterClothingItems() {
        CoroutineScope(Dispatchers.Main).launch {
            val filteredItems = withContext(Dispatchers.IO) {
                if (selectedCategories.isEmpty() && selectedTags.isEmpty()) {
                    ClothingItemsManager.getClothingItems()
                } else {
                    ClothingItemsManager.getClothingItems().filter { item ->
                        val typeMatches = selectedCategories.isEmpty() || item.categories.any { selectedCategories.contains(it) }
                        val tagMatches = selectedTags.isEmpty() || item.tags.any { it in selectedTags }
                        typeMatches && tagMatches
                    }
                }
            }
            displayClothingItems(filteredItems)
        }
    }

    private fun displayClothingItems(clothingItems: List<ClothingItem>) {
        if (clothingItems.isEmpty()) {
            noPiecesTextView.visibility = View.VISIBLE
            wardrobeRecyclerView.visibility = View.GONE
            return
        }
        noPiecesTextView.visibility = View.GONE
        wardrobeRecyclerView.visibility = View.VISIBLE
        clothingItemAdapter.updateItems(clothingItems)
    }

    private fun displayDetails(item: ClothingItem) {
        if (bottomSheetDialog != null && bottomSheetDialog!!.isShowing) {
            return
        }

        val view: View = layoutInflater.inflate(R.layout.clothing_item_details, null)
        bottomSheetDialog = BottomSheetDialog(requireContext()).apply {
            setContentView(view)
            delegate.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)?.let { bottomSheet ->
                BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        bottomSheetDialog?.show()

        val clothingItemImageView: ImageView = view.findViewById(R.id.clothingItemImageView)
        val categoriesLinearLayout: LinearLayout = view.findViewById(R.id.categoriesLinearLayout)
        val errorTextView: TextView = view.findViewById(R.id.errorTextView)
        val closeDialog: ImageButton = view.findViewById(R.id.closeDialog)

        CoroutineScope(Dispatchers.Main).launch {
            val bitmap = withContext(Dispatchers.IO) {
                ClothingItemsManager.getImage(requireContext(), item.imageName)
            }
            clothingItemImageView.setImageBitmap(bitmap)
        }

        val inflater = LayoutInflater.from(categoriesLinearLayout.context)
        categoriesLinearLayout.removeAllViews()

        if (item.categories.isNotEmpty()) {
            errorTextView.visibility = View.GONE
            for (category in item.categories) {
                val categoryView = inflater.inflate(R.layout.grid_item_category, categoriesLinearLayout, false)
                val categoryTextView: TextView = categoryView.findViewById(R.id.categoryTextView)
                categoryTextView.text = category
                categoriesLinearLayout.addView(categoryView)
            }
        } else {
            errorTextView.visibility = View.VISIBLE
        }

        closeDialog.setOnClickListener {
            bottomSheetDialog?.dismiss()
        }
    }
}
