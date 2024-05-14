package com.example.mywardrobe.ui.fragments

import android.graphics.Typeface
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
import com.example.mywardrobe.managers.ClothingTypesManager
import com.example.mywardrobe.managers.Tag

class CreateFragment : Fragment() {

    lateinit var generateButton: ImageButton
    private lateinit var outfitLinearLayout: LinearLayout
    private lateinit var typesLinearLayout: LinearLayout
    private lateinit var tagsLinearLayout: LinearLayout
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
        typesLinearLayout = view.findViewById(R.id.typesLinearLayout)
        tagsLinearLayout = view.findViewById(R.id.tagsLinearLayout)
        outfitLinearLayout = view.findViewById(R.id.outfitLinearLayout)

        outfit = mutableMapOf()

        val clothingItems = ClothingItemsManager.getClothingItems()
        val types = ClothingTypesManager.getTypes()
        val tags = ClothingTagsManager.getTags()

        displayTypesAndTags(types, tags, clothingItems)

        generateOutfit(clothingItems)

        generateButton.setOnClickListener {
            generateOutfit(clothingItems)
        }
    }

    fun displayTypesAndTags(types: List<String>, tags: List<Tag>, clothingItems: List<ClothingItem>){
        var typesIdCounter = 1
        for(type in types){
            val checkbox = CheckBox(requireContext())
            val checkboxLayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                90
            ).also { checkbox.layoutParams = it }
            checkboxLayoutParams.setMargins(0, 0, 20, 0)
            checkbox.setPadding(25, 15, 25, 15)
            checkbox.setTextColor(ContextCompat.getColor(requireContext(), R.color.font))
            checkbox.background = ContextCompat.getDrawable(requireContext(),
                R.drawable.radio_border
            )
            checkbox.buttonDrawable = null
            checkbox.text = type.toString()
            checkbox.textSize = 14f
            checkbox.id = typesIdCounter++
            checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
                toggleOutfitType(buttonView, outfit, clothingItems)
            }
            val typeItems = clothingItems.filter { ClothingTypesManager.getTypeName(it.type.toInt()) == type }
            if(typeItems.isNotEmpty()){
                checkbox.isChecked = true
            }

            typesLinearLayout.addView(checkbox)
        }

        var tagsIdCounter = 1
        for(tag in tags){
            val checkbox = CheckBox(requireContext())
            val checkboxLayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                90
            ).also { checkbox.layoutParams = it }
            checkboxLayoutParams.setMargins(0, 0, 20, 0)
            checkbox.setPadding(25, 15, 25, 15)
            checkbox.setTextColor(ContextCompat.getColor(requireContext(), R.color.font))
            checkbox.background = ContextCompat.getDrawable(requireContext(),
                R.drawable.radio_border
            )
            checkbox.buttonDrawable = null
            checkbox.text = tag.name.toString()
            checkbox.textSize = 14f
            checkbox.id = tagsIdCounter++

            tagsLinearLayout.addView(checkbox)
        }
    }



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
    }

    fun generateOutfit(clothingItems: List<ClothingItem>){
        val selectedTypes = mutableListOf<Int>()

        for (i in 0 until typesLinearLayout.childCount){
            val view = typesLinearLayout.getChildAt(i)
            if(view is CheckBox && view.isChecked){

                selectedTypes.add(view.id)
            }
        }

        if (selectedTypes.isEmpty()) {
            Toast.makeText(requireContext(), "Please select at least one clothing type", Toast.LENGTH_SHORT).show()
            return
        }

        val filteredItems = clothingItems.filter { selectedTypes.contains(it.type.toInt()) }

        outfit = mutableMapOf()
        for (type in selectedTypes) {
            val typeItems = filteredItems.filter { it.type.toInt() == type }
            if (typeItems.isNotEmpty()) {
                outfit[type] = typeItems.random()
            }
        }

        if (outfit.size < selectedTypes.size) {
            Toast.makeText(requireContext(), "Not enough pieces for selected types", Toast.LENGTH_SHORT).show()
            return
        }

       displayOutfit(outfit)
    }

    fun toggleOutfitType(buttonView: CompoundButton, outfit: MutableMap<Int, ClothingItem>, clothingItems: List<ClothingItem> ){
        if(!buttonView.isChecked){
            outfit.remove(buttonView.id)
            displayOutfit(outfit)
        }else{
            val typeItems = clothingItems.filter { it.type.toInt() == buttonView.id }
            if (typeItems.isEmpty()) {
                Toast.makeText(requireContext(), "You don't have any piece of this type", Toast.LENGTH_SHORT).show()
                return
            }
            outfit[buttonView.id] = typeItems.random()
            displayOutfit(outfit)
        }
    }

}