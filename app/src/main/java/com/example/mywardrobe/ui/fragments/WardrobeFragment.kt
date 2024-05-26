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
    private lateinit var layoutTypeButton: ImageButton
    private lateinit var wardrobeScrollView: ScrollView
    private lateinit var wardobeLinearLayout: LinearLayout
    private lateinit var typesLinearLayout: LinearLayout
    private lateinit var wardobeGridView: GridView
    private lateinit var noPiecesTextView: TextView

    private val selectedTypes = mutableListOf<Int>()
    private val selectedTags = mutableListOf<Int>()
    private lateinit var sharedPreferences: SharedPreferences

    var isLinear: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wardrobe, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutTypeButton = view.findViewById(R.id.layoutTypeButton)
        wardobeLinearLayout = view.findViewById(R.id.wardobeLinearLayout)
        wardrobeScrollView = view.findViewById(R.id.wardrobeScrollView)
        typesLinearLayout = view.findViewById(R.id.typesLinearLayout)
        wardobeGridView = view.findViewById(R.id.wardobeGridView)
        noPiecesTextView = view.findViewById(R.id.noPiecesTextView)


        sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        isLinear = sharedPreferences.getBoolean(PREF_IS_LINEAR, true)
        if(isLinear) {
            layoutTypeButton.setBackgroundResource(R.drawable.baseline_view_list_24)
        }else{
            layoutTypeButton.setBackgroundResource(R.drawable.baseline_grid_view_24)

        }
        val clothingItems = ClothingItemsManager.getClothingItems()
        displayClothingItems(clothingItems)
        //val clothingTypes = ClothingCategoriesManager.getTypes()
        //displayTypesAndTags(clothingTypes)

        layoutTypeButton.setOnClickListener {
            toggleLayout()
        }
    }

    private fun toggleLayout() {
        isLinear = !isLinear
        sharedPreferences.edit().putBoolean(PREF_IS_LINEAR, isLinear).apply()

        if(isLinear){
            layoutTypeButton.setBackgroundResource(R.drawable.baseline_view_list_24)
        }else{
            layoutTypeButton.setBackgroundResource(R.drawable.baseline_grid_view_24)
        }

        displayClothingItems(ClothingItemsManager.getClothingItems())
    }

    fun displayTypesAndTags(types: List<String>){
        var typeCheckboxIdCounter = 1
        for(type in types){
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
            checkbox.text = type.toString()
            checkbox.textSize = 14f
            checkbox.id = typeCheckboxIdCounter++
            checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
                typeChecked(buttonView, isChecked)
            }

            typesLinearLayout.addView(checkbox)
        }

    }

    private fun typeChecked(buttonView: CompoundButton?, checked: Boolean) {
        if(checked){
            selectedTypes.add(buttonView?.id as Int)
            buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.fontSecondary))
            for (selectedType in selectedTypes) {
                Log.d("WardobeFragment", "$selectedType")
            }
        }else{
            selectedTypes.remove(buttonView?.id as Int)
            buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.checkboxAccent))
        }
        //filterClothingItems()
    }

    private fun tagChecked(checked: Boolean, id: Int) {
        if(checked){
            selectedTags.add(id)
        }else{
            selectedTags.remove(id)
        }
        //filterClothingItems()
    }

//    private fun filterClothingItems() {
//        val filteredItems = if (selectedTypes.isEmpty() && selectedTags.isEmpty()) {
//            ClothingItemsManager.getClothingItems()
//        } else {
//            ClothingItemsManager.getClothingItems().filter { item ->
//                val typeMatches = selectedTypes.isEmpty() || selectedTypes.contains(item.type.toInt())
//                //val tagMatches = selectedTags.isEmpty() || item.categories.any { selectedTags.contains(it) }
//                typeMatches //&& tagMatches
//            }
//        }
//        wardobeLinearLayout.removeAllViews()
//        displayClothingItems(filteredItems)
//    }

    fun displayClothingItems(clothingItems: List<ClothingItem>){
        wardobeLinearLayout.removeAllViews()

        if(clothingItems.isEmpty()){
            noPiecesTextView.visibility = View.VISIBLE

            return
        }

        if(isLinear){
            noPiecesTextView.visibility = View.GONE
            wardobeGridView.visibility = View.GONE
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
                itemTypeTextView.text = item.categories[0]
                itemTypeTextView.background = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.rounded_border
                )
                itemTypeTextView.setPadding(20, 10, 20, 10)
                tagLinearLayout.addView(itemTypeTextView)

                for (category in item.categories) {
                    val itemTagTextView = TextView(requireContext())
                    itemTagTextView.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    itemTagTextView.textSize = 16f
                    itemTagTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.font))
                    itemTagTextView.text = category
                    itemTagTextView.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.rounded_border
                    )
                    itemTagTextView.setPadding(20, 10, 20, 10)

                    tagLinearLayout.addView(itemTagTextView)
                }

                outerLinearLayout.setOnClickListener {
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

                innerLinearLayout.addView(tagLinearLayout)
                outerLinearLayout.addView(imageView)
                outerLinearLayout.addView(innerLinearLayout)
                wardobeLinearLayout.addView(outerLinearLayout)

            }
            return
        }else{
            noPiecesTextView.visibility = View.GONE
            wardobeGridView.visibility = View.VISIBLE
            val wardrobeLayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (clothingItems.size/2)*500
            ).also { wardobeGridView.layoutParams = it }
            wardobeGridView.requestLayout()

            val adapter = ClothingItemAdapter(requireContext(), clothingItems)
            wardobeGridView.adapter = adapter

            wardobeGridView.setOnItemClickListener { parent, view, position, id ->
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

        }

    }

    fun byteArrayToBitmap(byteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

}