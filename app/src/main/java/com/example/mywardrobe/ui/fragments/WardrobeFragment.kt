package com.example.mywardrobe.ui.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.mywardrobe.R
import com.example.mywardrobe.managers.ClothingItem
import com.example.mywardrobe.managers.ClothingItemsManager
import com.example.mywardrobe.managers.ClothingTagsManager
import com.example.mywardrobe.managers.ClothingTypesManager
import com.example.mywardrobe.managers.Tag

class WardrobeFragment : Fragment() {

    private lateinit var newClothingButton: ImageButton
    private lateinit var wardobeLinearLayout: LinearLayout
    private lateinit var tagsAndTypesLinearLayout: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wardrobe, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newClothingButton = view.findViewById(R.id.addNewClothingButton)
        wardobeLinearLayout = view.findViewById(R.id.wardobeLinearLayout)
        tagsAndTypesLinearLayout = view.findViewById(R.id.tagsAndTypesLinearLayout)

        newClothingButton.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentFrame, NewClothingFragment())
                .addToBackStack(null)
                .commit()
        }

        val clothingItems = ClothingItemsManager.getClothingItems()
        displayClothingItems(clothingItems)
        val clothingTypes = ClothingTypesManager.getTypes()
        val clothingTags = ClothingTagsManager.getTags()
        displayTypesAndTags(clothingTypes, clothingTags)
    }

    fun displayTypesAndTags(types: List<String>, tags: List<Tag>){
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

    fun displayClothingItems(clothingItems: List<ClothingItem>){
        if(clothingItems.isEmpty()){
            val linearLayout = LinearLayout(requireContext())
            val linearLayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
            ).also { linearLayout.layoutParams = it }
            linearLayoutParams.setMargins(0, 0, 0, 50)
            linearLayout.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_border)
            linearLayout.setPadding(40, 30, 40, 30)

            val text = TextView(requireContext())
            text.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
            )
            text.setTextColor(ContextCompat.getColor(requireContext(), R.color.font))
            text.textSize = 16f
            text.textAlignment = View.TEXT_ALIGNMENT_CENTER
            text.text = "You don't have any pieces yet"

            linearLayout.addView(text)
            wardobeLinearLayout.addView(linearLayout)

            return
        }

        for (item in clothingItems) {
            val outerLinearLayout = LinearLayout(requireContext())
            val outerLayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).also { outerLinearLayout.layoutParams = it }
            outerLayoutParams.setMargins(0, 0, 0, 50)
            outerLinearLayout.orientation = LinearLayout.HORIZONTAL

            val imageView = ImageView(requireContext())
            imageView.layoutParams = LinearLayout.LayoutParams(
                230,
                230
            )
            imageView.setImageBitmap(
                ClothingItemsManager.getImage(
                    requireContext(),
                    item.imageName
                )
            )
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP

            val innerLinearLayout = LinearLayout(requireContext())
            val innerLayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            ).also { innerLinearLayout.layoutParams = it }
            innerLayoutParams.setMargins(70, 0, 0, 0)
            innerLinearLayout.orientation = LinearLayout.VERTICAL


            val itemNameTextView = TextView(requireContext())
            val itemNameLayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).also { itemNameTextView.layoutParams = it }
            itemNameLayoutParams.setMargins(0, 0, 0, 10)
            itemNameTextView.textSize = 20f
            itemNameTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.font))
            itemNameTextView.text = item.name

            val tagLinearLayout = LinearLayout(requireContext())
            tagLinearLayout.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            tagLinearLayout.orientation = LinearLayout.HORIZONTAL

            val itemTypeTextView = TextView(requireContext())
            val itemTypeParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).also { itemTypeTextView.layoutParams = it }
            itemTypeParams.setMargins(0, 0, 20, 0)
            itemTypeTextView.textSize = 16f
            itemTypeTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.font))
            itemTypeTextView.text = ClothingTypesManager.getTypeName(item.type.toInt())
            itemTypeTextView.background = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.rounded_border
            )
            itemTypeTextView.setPadding(20, 10, 20, 10)
            tagLinearLayout.addView(itemTypeTextView)

            for (tag in item.tags) {
                val itemTagTextView = TextView(requireContext())
                itemTagTextView.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                itemTagTextView.textSize = 16f
                itemTagTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.font))
                itemTagTextView.text = ClothingTagsManager.getTagName(tag.toInt())
                itemTagTextView.background = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.rounded_border
                )
                itemTagTextView.setPadding(20, 10, 20, 10)

                tagLinearLayout.addView(itemTagTextView)

            }

            innerLinearLayout.addView(itemNameTextView)
            innerLinearLayout.addView(tagLinearLayout)
            outerLinearLayout.addView(imageView)
            outerLinearLayout.addView(innerLinearLayout)
            wardobeLinearLayout.addView(outerLinearLayout)
        }

    }

    fun byteArrayToBitmap(byteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

}