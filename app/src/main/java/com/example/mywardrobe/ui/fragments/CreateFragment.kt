package com.example.mywardrobe.ui.fragments

import android.content.Context
import android.graphics.Typeface
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.HorizontalScrollView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.mywardrobe.R
import com.example.mywardrobe.managers.ClothingItem
import com.example.mywardrobe.managers.ClothingItemsManager
import com.example.mywardrobe.managers.ClothingTagsManager
import com.example.mywardrobe.managers.ClothingCategoriesManager
import com.example.mywardrobe.managers.Outfit
import com.example.mywardrobe.managers.OutfitManager
import com.example.mywardrobe.managers.Tag
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CreateFragment : Fragment() {

    lateinit var generateButton: ImageButton
    lateinit var saveButton: ImageButton
    lateinit var filtersImageButton: ImageButton
    private lateinit var outfitLinearLayout: LinearLayout
    private lateinit var outfit: MutableMap<String, ClothingItem>

    private var bottomSheetDialog: BottomSheetDialog? = null

    private lateinit var selectedTypes: MutableList<String>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        generateButton = view.findViewById(R.id.generateButton)
        saveButton = view.findViewById(R.id.saveButton)
        filtersImageButton = view.findViewById(R.id.filtersImageButton)
        outfitLinearLayout = view.findViewById(R.id.outfitLinearLayout)

        outfit = mutableMapOf()
        selectedTypes = mutableListOf("Tops", "Bottoms", "Footwear")


        val selectedOutfitJson = arguments?.getString("outfit")
        if(selectedOutfitJson != null){
            val selectedOutfit = Gson().fromJson(selectedOutfitJson, Outfit::class.java)
            outfit = selectedOutfit.items.associateBy { it.type }.toMutableMap()
            displayOutfit(outfit)
            saveButton.setImageResource(R.drawable.baseline_favorite_24)
        }else {
            generateOutfit()
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

    fun openModal(){
        if(bottomSheetDialog != null && bottomSheetDialog!!.isShowing){
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

        for(type in types){
            val categoryView = inflater.inflate(R.layout.grid_item_category, typesFilterLinearLayout, false)
            val categoryTextView: TextView = categoryView.findViewById(R.id.categoryTextView)
            categoryTextView.text = type
            val typeImage: ImageView = categoryView.findViewById(R.id.categoryImage)
            if(newSelectedTypes.contains(type)){
                typeImage.setBackgroundResource(R.drawable.category_selector_checked)
                categoryView.isSelected = true
                if(!newSelectedTypes.contains(type)){
                    newSelectedTypes.add(type)
                }
            }

            categoryView.setOnClickListener {
                if(categoryView.isSelected){
                    newSelectedTypes.remove(type)
                    typeImage.setBackgroundResource(R.drawable.category_selector)
                } else {
                    if(!newSelectedTypes.contains(type)){
                        newSelectedTypes.add(type)
                    }
                    typeImage.setBackgroundResource(R.drawable.category_selector_checked)
                }
                categoryView.isSelected = !categoryView.isSelected
            }

            typesFilterLinearLayout.addView(categoryView)
        }

        applyChanges.setOnClickListener {
            selectedTypes = newSelectedTypes
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

        outfit = mutableMapOf()
        for (type in selectedTypes) {
            val typeItems = filteredItems.filter { it.type == type }
            if (typeItems.isNotEmpty()) {
                outfit[type] = typeItems.random()
            }
        }

        if (outfit.size < selectedTypes.size) {
            displayOutfit(outfit)
            return
        }

        displayOutfit(outfit)
    }

    fun displayOutfit(outfit: MutableMap<String, ClothingItem>) {
        outfitLinearLayout.removeAllViews()

        val itemTypes = ClothingCategoriesManager.getAllTypes()

        val sortedOutfit = outfit.toSortedMap(compareBy { itemTypes.indexOf(it) })

        for (type in itemTypes) {
            val itemImageView = ImageView(requireContext())
            val itemImageViewLayoutParams = LinearLayout.LayoutParams(
                350,
                350
            ).also { itemImageView.layoutParams = it }
            itemImageViewLayoutParams.setMargins(0, 0, 0, 10)
            itemImageViewLayoutParams.gravity = Gravity.CENTER

            if (selectedTypes.contains(type) && sortedOutfit.containsKey(type)) {
                val item = sortedOutfit[type]
                itemImageView.setImageBitmap(ClothingItemsManager.getImage(requireContext(), item?.imageName as String))
                itemImageView.scaleType = ImageView.ScaleType.CENTER_CROP
                outfitLinearLayout.addView(itemImageView)
            } else {
                if (selectedTypes.contains(type)) {
                    val addButton = Button(requireContext())
                    val addButtonLayoutParams = LayoutParams(
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT
                    ).also { addButton.layoutParams = it }
                    addButtonLayoutParams.setMargins(0, 20, 0, 20)
                    addButton.text = "Add piece"
                    addButton.setOnClickListener {
                        Toast.makeText(requireContext(), "Dodaj element dla $type", Toast.LENGTH_SHORT).show()
                    }
                    outfitLinearLayout.addView(addButton)
                }
            }
        }

        val newOutfit = Outfit(
            id = OutfitManager.generateId(),
            items = outfit.values.toList()
        )

        val outfitExists = OutfitManager.outfitExists(newOutfit)

        if (outfitExists) {
            saveButton.setImageResource(R.drawable.baseline_favorite_24)
        } else {
            saveButton.setImageResource(R.drawable.baseline_favorite_border_24)
        }
    }



    private fun saveOutfit(outfit: MutableMap<String, ClothingItem>){
        val newOutfit = Outfit(
            id = OutfitManager.generateId(),
            items = outfit.values.toList()
        )

        val outfitExists = OutfitManager.outfitExists(newOutfit)

        if(outfitExists){
            OutfitManager.removeOutfit(newOutfit)
        }else{
            OutfitManager.addOutfit(newOutfit)
        }
        val outfits = OutfitManager.getOutfits()

        GlobalScope.launch(Dispatchers.Main) {
            val dataResult = OutfitManager.saveOutfits(requireContext(), outfits)
            if(dataResult){
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