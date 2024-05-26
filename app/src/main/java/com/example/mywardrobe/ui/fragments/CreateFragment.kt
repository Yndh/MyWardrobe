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
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import android.widget.Toast
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
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CreateFragment : Fragment() {

    lateinit var generateButton: ImageButton
    lateinit var saveButton: ImageButton
    private lateinit var outfitLinearLayout: LinearLayout
    private lateinit var categoriesLinearLayout: LinearLayout
    private lateinit var outfit: MutableMap<Int, ClothingItem>

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
        categoriesLinearLayout = view.findViewById(R.id.categoriesLinearLayout)
        outfitLinearLayout = view.findViewById(R.id.outfitLinearLayout)

        outfit = mutableMapOf()

        val clothingItems = ClothingItemsManager.getClothingItems()
        //val types = ClothingCategoriesManager.getCategories()

        //displayCategories(types, clothingItems)

        val selectedOutfitJson = arguments?.getString("outfit")
        if(selectedOutfitJson != null){
            val selectedOutfit = Gson().fromJson(selectedOutfitJson, Outfit::class.java)
//            outfit = selectedOutfit.items.associateBy { it.type.toInt() }.toMutableMap()
            displayOutfit(outfit)
            saveButton.setImageResource(R.drawable.baseline_favorite_24)
        }else {
            //generateOutfit(clothingItems)
        }

        generateButton.setOnClickListener {
            //generateOutfit(clothingItems)
        }

        saveButton.setOnClickListener {
            saveOutfit(outfit)
        }
    }

    fun displayCategories(categories: List<String>, clothingItems: List<ClothingItem>) {
        var categoriesIdCounter = 1
        for (category in categories) {
            val checkbox = CheckBox(requireContext())
            val checkboxLayoutParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            ).also { checkbox.layoutParams = it }
            checkboxLayoutParams.setMargins(0, 0, 20, 0)
            checkbox.setPadding(25, 15, 25, 15)
            checkbox.setTextColor(ContextCompat.getColor(requireContext(), R.color.font))
            checkbox.background = ContextCompat.getDrawable(requireContext(), R.drawable.radio_border)
            checkbox.buttonDrawable = null
            checkbox.text = category
            checkbox.textSize = 14f
            checkbox.id = categoriesIdCounter++
            checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
//                toggleOutfitCategory(buttonView, outfit, clothingItems)
            }

            categoriesLinearLayout.addView(checkbox)
        }
    }

//    fun toggleOutfitCategory(buttonView: CompoundButton, outfit: MutableMap<Int, ClothingItem>, clothingItems: List<ClothingItem> ){
//        if(!buttonView.isChecked){
//            outfit.remove(buttonView.id)
//            displayOutfit(outfit)
//        }else{
//            val typeItems = clothingItems.filter { it.type.toInt() == buttonView.id }
//            if (typeItems.isEmpty()) {
//                Toast.makeText(requireContext(), "You don't have any piece of this type", Toast.LENGTH_SHORT).show()
//                return
//            }
//            outfit[buttonView.id] = typeItems.random()
//            displayOutfit(outfit)
//        }
//    }



    fun displayOutfit(outfit: MutableMap<Int, ClothingItem>){
        outfitLinearLayout.removeAllViews()
        for((key, item) in outfit.toSortedMap()){
            val itemImageView = ImageView(requireContext())
            val itemImageViewLayoutParams = LinearLayout.LayoutParams(
                350,
                350
            ).also{ itemImageView.layoutParams = it }
            itemImageViewLayoutParams.setMargins(0, 0, 0, 10)
            itemImageViewLayoutParams.gravity = Gravity.CENTER
            itemImageView.setImageBitmap(ClothingItemsManager.getImage(requireContext(), item.imageName))
            itemImageView.scaleType = ImageView.ScaleType.CENTER_CROP


            outfitLinearLayout.addView(itemImageView)
        }

        val newOutfit = Outfit(
            id = OutfitManager.generateId(),
            items = outfit.values.toList()
        )

        val outfitExists = OutfitManager.outfitExists(newOutfit)

        if(outfitExists){
            saveButton.setImageResource(R.drawable.baseline_favorite_24)
        }else{
            saveButton.setImageResource(R.drawable.baseline_favorite_border_24)
        }
    }

//    fun generateOutfit(clothingItems: List<ClothingItem>){
//        val selectedTypes = mutableListOf<Int>()
//
//        for (i in 0 until categoriesLinearLayout.childCount) {
//            val view = categoriesLinearLayout.getChildAt(i)
//            if (view is CheckBox && view.isChecked) {
//                //selectedCategories.add(view.text.toString())
//            }
//        }
//
//        if (selectedTypes.isEmpty()) {
//            Toast.makeText(requireContext(), "Please select at least one clothing type", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        val filteredItems = clothingItems.filter { selectedTypes.contains(it.type.toInt()) }
//
//        outfit = mutableMapOf()
//        for (type in selectedTypes) {
//            val typeItems = filteredItems.filter { it.type.toInt() == type }
//            if (typeItems.isNotEmpty()) {
//                outfit[type] = typeItems.random()
//            }
//        }
//
//        if (outfit.size < selectedTypes.size) {
//            Toast.makeText(requireContext(), "Not enough pieces for selected types", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//       displayOutfit(outfit)
//    }

    private fun saveOutfit(outfit: MutableMap<Int, ClothingItem>){
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