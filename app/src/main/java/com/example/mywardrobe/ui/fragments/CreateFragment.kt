package com.example.mywardrobe.ui.fragments


import StylingItemsAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mywardrobe.R
import com.example.mywardrobe.managers.ClothingItem
import com.example.mywardrobe.managers.ClothingItemsManager
import com.example.mywardrobe.managers.ClothingCategoriesManager
import com.example.mywardrobe.managers.Outfit
import com.example.mywardrobe.managers.OutfitManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

data class OutfitClothingItem(
    val item: ClothingItem,
    var isLocked: Boolean = false
)

class CreateFragment : Fragment() {

    private lateinit var generateButton: ImageButton
    private lateinit var saveButton: ImageButton
    private lateinit var filtersImageButton: ImageButton
    private lateinit var outfitLinearLayout: LinearLayout
    private lateinit var outfitRecyclerView: RecyclerView

    private var bottomSheetDialog: BottomSheetDialog? = null

    private lateinit var outfit: MutableMap<String, OutfitClothingItem>
    private lateinit var selectedTypes: MutableList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        generateButton = view.findViewById(R.id.generateButton)
        saveButton = view.findViewById(R.id.saveButton)
        filtersImageButton = view.findViewById(R.id.filtersImageButton)
        outfitLinearLayout = view.findViewById(R.id.outfitLinearLayout)
        outfitRecyclerView = view.findViewById(R.id.outfitRecyclerView)

        outfit = mutableMapOf()
        selectedTypes = mutableListOf("Tops", "Bottoms", "Footwear")

        val selectedOutfitJson = arguments?.getString("outfit")
        if (selectedOutfitJson != null) {
            val selectedOutfit = Gson().fromJson(selectedOutfitJson, Outfit::class.java)
            for (item in selectedOutfit.items) {
                if (!selectedTypes.contains(item.type)) {
                    selectedTypes.add(item.type)
                }
            }
            outfit = selectedOutfit.items.associateBy { it.type }
                .mapValues { OutfitClothingItem(it.value) }.toMutableMap()
            displayOutfit(outfit)
            saveButton.setImageResource(R.drawable.baseline_favorite_24)
        } else {
            generateOutfit()
        }
        val lockedItemJson = arguments?.getString("LockedItem")
        if (lockedItemJson != null) {
            val lockedItem = Gson().fromJson(lockedItemJson, ClothingItem::class.java)
            outfit[lockedItem.type] = OutfitClothingItem(lockedItem, isLocked = true)
            generateOutfit()
            if (!selectedTypes.contains(lockedItem.type)) {
                selectedTypes.add(lockedItem.type)
            }
        }

        generateButton.setOnClickListener {
            generateOutfit()
        }

        saveButton.setOnClickListener {
            saveOutfit(outfit)
        }

        filtersImageButton.setOnClickListener {
            openModal()
        }
    }

    fun openModal() {
        if (bottomSheetDialog != null && bottomSheetDialog!!.isShowing) {
            return
        }

        val types = ClothingCategoriesManager.getAllTypes()

        val view: View = layoutInflater.inflate(R.layout.create_fragment_filters, null)
        bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog?.setContentView(view)
        bottomSheetDialog?.show()

        val applyChanges: AppCompatButton = view.findViewById(R.id.applyChanges)
        val typesFilterLinearLayout: LinearLayout = view.findViewById(R.id.typesFilterLinearLayout)
        val selectFilterCategories: LinearLayout = view.findViewById(R.id.selectFilterCategories)
        val filterCategoriesHorizontalScrollView: HorizontalScrollView = view.findViewById(R.id.filterCategoriesHorizontalScrollView)
        val filterCategoriesHorizontalLinearLayout: LinearLayout = view.findViewById(R.id.filterCategoriesHorizontalLinearLayout)
        val closeFilterDialog: ImageButton = view.findViewById(R.id.closeFilterDialog)

        filterCategoriesHorizontalLinearLayout.removeAllViews()

        val inflater = LayoutInflater.from(typesFilterLinearLayout.context)

        val newSelectedTypes = selectedTypes

        for (type in types) {
            val categoryView = inflater.inflate(R.layout.grid_item_category, typesFilterLinearLayout, false)
            val categoryTextView: TextView = categoryView.findViewById(R.id.categoryTextView)
            categoryTextView.text = type
            val typeImage: ImageView = categoryView.findViewById(R.id.categoryImage)
            if (newSelectedTypes.contains(type)) {
                typeImage.setBackgroundResource(R.drawable.category_selector_checked)
                categoryView.isSelected = true
                if (!newSelectedTypes.contains(type)) {
                    newSelectedTypes.add(type)
                }
            }

            categoryView.setOnClickListener {
                if (categoryView.isSelected) {
                    newSelectedTypes.remove(type)
                    typeImage.setBackgroundResource(R.drawable.category_selector)
                } else {
                    if (!newSelectedTypes.contains(type)) {
                        newSelectedTypes.add(type)
                    }
                    typeImage.setBackgroundResource(R.drawable.category_selector_checked)
                }
                categoryView.isSelected = !categoryView.isSelected
            }

            typesFilterLinearLayout.addView(categoryView)
        }

        applyChanges.setOnClickListener {
            val typesToRemove = types.filter { !newSelectedTypes.contains(it) }

            selectedTypes = selectedTypes.filter { type ->
                !typesToRemove.contains(type)
            }.toMutableList()

            outfit = outfit.filter { (type, _) ->
                !typesToRemove.contains(type)
            }.toMutableMap()

            bottomSheetDialog?.dismiss()
            generateOutfit()
        }


        closeFilterDialog.setOnClickListener {
            bottomSheetDialog?.dismiss()
        }
    }

    fun generateOutfit() {
        if (selectedTypes.isEmpty()) {
            Toast.makeText(requireContext(), "Please select at least one clothing type", Toast.LENGTH_SHORT).show()
            return
        }

        val clothingItems = ClothingItemsManager.getClothingItems()

        val filteredItems = clothingItems.filter { selectedTypes.contains(it.type) }

        val newOutfit = mutableMapOf<String, OutfitClothingItem>()
        for ((type, item) in outfit) {
            if (item.isLocked) {
                newOutfit[type] = item
            }
        }

        for (type in selectedTypes) {
            if (!newOutfit.containsKey(type)) {
                val typeItems = filteredItems.filter { it.type == type }
                if (typeItems.isNotEmpty()) {
                    newOutfit[type] = OutfitClothingItem(typeItems.random())
                }
            }
        }

        outfit = newOutfit

        displayOutfit(outfit)
    }

    private fun displayOutfit(outfit: MutableMap<String, OutfitClothingItem>) {
        val itemTypes = ClothingCategoriesManager.getAllTypes()
        val sortedOutfit = itemTypes.mapNotNull { outfit[it] }

        outfitRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        outfitRecyclerView.adapter = StylingItemsAdapter(requireContext(), sortedOutfit)

        val newOutfit = Outfit(id = OutfitManager.generateId(), items = outfit.values.map { it.item })
        saveButton.setImageResource(
            if (OutfitManager.outfitExists(newOutfit)) R.drawable.baseline_favorite_24
            else R.drawable.baseline_favorite_border_24
        )
    }

    private fun saveOutfit(outfit: MutableMap<String, OutfitClothingItem>) {
        val newOutfit = Outfit(
            id = OutfitManager.generateId(),
            items = outfit.values.map { it.item }
        )

        val outfitExists = OutfitManager.outfitExists(newOutfit)

        if (outfitExists) {
            OutfitManager.removeOutfit(newOutfit)
        } else {
            OutfitManager.addOutfit(newOutfit)
        }
        val outfits = OutfitManager.getOutfits()

        GlobalScope.launch(Dispatchers.Main) {
            val dataResult = OutfitManager.saveOutfits(requireContext(), outfits)
            if (dataResult) {
                Log.d("CreateFragment", "Outfit file saved")
                if (outfitExists) {
                    saveButton.setImageResource(R.drawable.baseline_favorite_border_24)
                    Toast.makeText(requireContext(), "Outfit has been removed", Toast.LENGTH_SHORT).show()
                } else {
                    saveButton.setImageResource(R.drawable.baseline_favorite_24)
                    Toast.makeText(requireContext(), "Outfit has been saved", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Failed to save item", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
