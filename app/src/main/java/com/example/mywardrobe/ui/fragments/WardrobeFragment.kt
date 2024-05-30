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
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
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
import kotlin.math.abs
import kotlin.math.min

class WardrobeFragment : Fragment() {
    private lateinit var categoriesRadioGroup: RadioGroup
    private lateinit var wardrobeContainerLinearLayout: LinearLayout
    private lateinit var wardrobeScrollView: ScrollView
    private lateinit var clothingItemAdapters: MutableMap<String, ClothingItemAdapter>

    private val viewHolders = mutableMapOf<String, View>()
    private val categoryOrder = mutableListOf<String>()

    private var bottomSheetDialog: BottomSheetDialog? = null
    private var selectedCategory: String? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_wardrobe, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        categoriesRadioGroup = view.findViewById(R.id.categoriesRadioGroup)
        wardrobeContainerLinearLayout = view.findViewById(R.id.wardrobeContainerLinearLayout)
        wardrobeScrollView = view.findViewById(R.id.wardrobeScrollView)

        clothingItemAdapters = mutableMapOf()

        CoroutineScope(Dispatchers.Main).launch {
            val clothingItems = withContext(Dispatchers.IO) { ClothingItemsManager.getClothingItems() }
            val categories = withContext(Dispatchers.IO) { ClothingCategoriesManager.getCategories() }
            displayCategories(categories, clothingItems)
            displayClothingItems(clothingItems)
        }

        wardrobeScrollView.viewTreeObserver.addOnScrollChangedListener {
            onScrollChanged()
        }
    }

    private fun displayCategories(categories: Map<String, List<String>>, clothingItems: List<ClothingItem>) {
        var typeRadioIdCounter = 1

        val existingCategories = clothingItems.flatMap { it.categories }.toSet()

        for (category in categories.keys) {
            if (category in existingCategories) {
                categoryOrder.add(category)

                val radioButton = RadioButton(requireContext()).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply { setMargins(0, 0, 20, 0) }
                    setPadding(50, 30, 50, 30)
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.checkboxAccent))
                    background = ContextCompat.getDrawable(requireContext(), R.drawable.radio_border)
                    buttonDrawable = null
                    text = category
                    textSize = 12f
                    id = typeRadioIdCounter++
                    tag = category
                    setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) {
                            setTextColor(ContextCompat.getColor(requireContext(), R.color.fontSecondary))
                            categorySelected(category)
                        }else{
                            setTextColor(ContextCompat.getColor(requireContext(), R.color.checkboxAccent))
                        }
                    }
                }
                categoriesRadioGroup.addView(radioButton)
            }
        }
    }

    private fun categorySelected(category: String) {
        selectedCategory = category
        scrollToCategory(category)
    }

    private fun scrollToCategory(category: String) {
        val targetView = viewHolders[category]
        targetView?.let {
            wardrobeScrollView.post {
                wardrobeScrollView.smoothScrollTo(0, it.top)
            }
        }
    }

    private fun onScrollChanged() {
        val scrollY = wardrobeScrollView.scrollY
        var closestCategory: String? = null
        var closestDistance = Int.MAX_VALUE


        for ((category, view) in viewHolders) {
            val categoryTop = view.top
            val categoryBottom = view.bottom
            if (categoryTop <= wardrobeScrollView.height && categoryBottom >= 0) {
                val distance = min(abs(categoryTop - scrollY), abs(categoryBottom - scrollY))
                if (distance < closestDistance) {
                    closestCategory = category
                    closestDistance = distance
                }
            }
        }

        closestCategory?.let {
            if (it != selectedCategory) {
                selectedCategory = it
                categoriesRadioGroup.findViewWithTag<RadioButton>(it)?.isChecked = true
            }
        }
    }


    private fun displayClothingItems(clothingItems: List<ClothingItem>) {
        wardrobeContainerLinearLayout.removeAllViews()
        clothingItemAdapters.clear()
        viewHolders.clear()

        val itemsByCategory = clothingItems.flatMap { item ->
            item.categories.map { category -> category to item }
        }.groupBy({ it.first }, { it.second })

        for (category in categoryOrder) {
            val items = itemsByCategory[category] ?: continue

            val categoryTextView = TextView(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(0, 20, 0, 10) }
                text = category
                textSize = 16f
                typeface = ResourcesCompat.getFont(requireContext(), R.font.poppins_medium)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.font))
                setPadding(16, 16, 16, 16)
            }

            val recyclerView = RecyclerView(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                layoutManager = GridLayoutManager(requireContext(), 2)
            }

            val adapter = ClothingItemAdapter(requireContext(), items) { item -> displayDetails(item) }
            recyclerView.adapter = adapter
            clothingItemAdapters[category] = adapter

            val container = LinearLayout(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(0, 10, 0, 10) }
                orientation = LinearLayout.VERTICAL
                addView(categoryTextView)
                addView(recyclerView)
            }

            wardrobeContainerLinearLayout.addView(container)
            viewHolders[category] = container
        }
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

        clothingItemImageView.setImageBitmap(ClothingItemsManager.getImage(requireContext(), item.imageName))

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
