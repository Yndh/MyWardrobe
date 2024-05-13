package com.example.mywardrobe.ui.fragments

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
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

    lateinit var headwearImageView: ImageView
    lateinit var topsImageView: ImageView
    lateinit var bottomsImageView: ImageView
    lateinit var footwearImageView: ImageView
    lateinit var generateButton: ImageButton
    private lateinit var tagsAndTypesLinearLayout: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        headwearImageView = view.findViewById(R.id.headwearImageView)
        topsImageView = view.findViewById(R.id.topsImageView)
        bottomsImageView = view.findViewById(R.id.bottomsImageView)
        footwearImageView = view.findViewById(R.id.footwearImageView)
        generateButton = view.findViewById(R.id.generateButton)
        tagsAndTypesLinearLayout = view.findViewById(R.id.tagsAndTypesLinearLayout)

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
            val typeItems = clothingItems.filter { ClothingTypesManager.getTypeName(it.type.toInt()) == type }
            if(typeItems.isNotEmpty()){
                checkbox.isChecked = true
            }

            tagsAndTypesLinearLayout.addView(checkbox)
        }

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

            tagsAndTypesLinearLayout.addView(checkbox)
        }

        val button = Button(requireContext())
        val buttonLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            90
        ).also { button.layoutParams = it }
        buttonLayoutParams.setMargins(10, 0, 20, 0)
        button.setPadding(25, 15, 25, 15)
        button.setTextColor(ContextCompat.getColor(requireContext(), R.color.font))
        button.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_border)
        button.text = "Create New Tag"
        button.isAllCaps = false
        button.textSize = 14f
        button.setTypeface(null, Typeface.BOLD)
        button.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentFrame, NewClothingTagFragment())
                .addToBackStack(null)
                .commit()
        }

        tagsAndTypesLinearLayout.addView(button)

    }


    fun generateOutfit(clothingItems: List<ClothingItem>){
        for (item in clothingItems) {
            Log.d("ClothingItem", item.toString())
        }
        val headwearList = clothingItems.filter { it.type.toInt() == 1 }
        val topsList = clothingItems.filter { it.type.toInt() == 2 }
        val bottomsList = clothingItems.filter { it.type.toInt() == 3 }
        val footwearList = clothingItems.filter { it.type.toInt() == 4 }


        Log.d("FilteredLists", "Headwear List: $headwearList")
        Log.d("FilteredLists", "Tops List: $topsList")
        Log.d("FilteredLists", "Bottoms List: $bottomsList")
        Log.d("FilteredLists", "Footwear List: $footwearList")

        if (headwearList.isEmpty() || topsList.isEmpty() || bottomsList.isEmpty() || footwearList.isEmpty()) {
            Toast.makeText(requireContext(), "Not enough items for a complete outfit", Toast.LENGTH_SHORT).show()
            return
        }

        val headwear = headwearList.randomOrNull()
        val top = topsList.randomOrNull()
        val bottom = bottomsList.randomOrNull()
        val footwear = footwearList.randomOrNull()

        headwearImageView.setImageBitmap(
            ClothingItemsManager.getImage(requireContext(), headwear?.imageName as String)
        )
        topsImageView.setImageBitmap(
            ClothingItemsManager.getImage(requireContext(), top?.imageName as String)
        )
        bottomsImageView.setImageBitmap(
            ClothingItemsManager.getImage(requireContext(), bottom?.imageName as String)
        )
        footwearImageView.setImageBitmap(
            ClothingItemsManager.getImage(requireContext(), footwear?.imageName as String)
        )
    }
}