package com.example.mywardrobe.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.example.mywardrobe.R
import com.example.mywardrobe.managers.ClothingItem
import com.example.mywardrobe.managers.ClothingItemsManager
import com.example.mywardrobe.managers.ClothingTagsManager
import com.example.mywardrobe.managers.ClothingCategoriesManager
import com.example.mywardrobe.managers.Tag
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ClothingItemDetailsFragment : Fragment() {
    lateinit var goBackImageButton: ImageButton
    lateinit var imageButton: ImageButton
    lateinit var titleEditText: EditText
    lateinit var itemTypeRadioGroup: RadioGroup
    lateinit var tagsLinearLayout: LinearLayout
    lateinit var noTagsLinearLayout: LinearLayout
    lateinit var saveClothingButton: AppCompatButton
    lateinit var removeClothingButton: AppCompatButton
    lateinit var selectedClothingItem: ClothingItem

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_clothing_item_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        goBackImageButton = view.findViewById(R.id.goBackImageButton)
        imageButton = view.findViewById(R.id.imageButton)
        titleEditText = view.findViewById(R.id.titleEditText)
        itemTypeRadioGroup = view.findViewById(R.id.itemTypeRadioGroup)
        tagsLinearLayout = view.findViewById(R.id.tagsLinearLayout)
        noTagsLinearLayout = view.findViewById(R.id.noTagsLinearLayout)
        saveClothingButton = view.findViewById(R.id.saveClothingButton)
        removeClothingButton = view.findViewById(R.id.removeClothingButton)


        val selectedClothingItemJson = arguments?.getString("clothingItem")
        if (selectedClothingItemJson != null) {
            selectedClothingItem = Gson().fromJson(selectedClothingItemJson, ClothingItem::class.java)
        }else{
            Toast.makeText(requireContext(), "Failed getting piece details", Toast.LENGTH_SHORT).show()
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.beginTransaction().apply {
                replace(R.id.fragmentFrame, WardrobeFragment())
                commit()
            }
        }

        //val types = ClothingCategoriesManager.getTypes()
        //val tags = ClothingTagsManager.getTags()
        //displayClothingItem(types, tags, selectedClothingItem)

        saveClothingButton.setOnClickListener {
//            saveClothing(selectedClothingItem)
        }

        removeClothingButton.setOnClickListener {
            removeClothing(selectedClothingItem)
        }

        goBackImageButton.setOnClickListener {
            goBack()
        }
    }

//    private fun displayClothingItem(types: List<String>, tags: List<Tag>, clothingItem: ClothingItem ){
//        imageButton.setImageBitmap(ClothingItemsManager.getImage(requireContext(), clothingItem.imageName))
//
//
//        var radioButtonIdCounter = 1
//        for(type in types){
//            val radioButton = RadioButton(requireContext())
//            val radioButtonLayoutParams = LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT,
//                120
//            ).also { radioButton.layoutParams = it }
//            radioButtonLayoutParams.setMargins(0, 0, 20, 0)
//            radioButton.setPadding(25, 15, 25, 15)
//            radioButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.font))
//            radioButton.background = ContextCompat.getDrawable(requireContext(),
//                R.drawable.radio_border
//            )
//            radioButton.buttonDrawable = null
//            radioButton.text = type.toString()
//            radioButton.textSize = 14f
//            radioButton.id = radioButtonIdCounter++
//
//              if(clothingItem.type.toInt() == radioButton.id){
//                radioButton.isChecked = true
//            }
//
//            itemTypeRadioGroup.addView(radioButton)
//        }
//
//        if(tags.isEmpty()){
//            val textView = TextView(requireContext())
//            val textViewLayoutParams = LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                120
//            ).also { textView.layoutParams = it }
//            textView.setPadding(25,15,25,15)
//            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.font))
//            textView.background = ContextCompat.getDrawable(requireContext(),
//                R.drawable.rounded_border
//            )
//            textView.text = "You don't have any tags"
//            textView.textSize = 14f
//            textView.gravity = Gravity.CENTER
//            textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
//
//            noTagsLinearLayout.addView(textView)
//            return
//        }
//        for(tag in tags){
//            val checkbox = CheckBox(requireContext())
//            val checkboxLayoutParams = LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT,
//                120
//            ).also { checkbox.layoutParams = it }
//            checkboxLayoutParams.setMargins(0, 0, 20, 0)
//            checkbox.setPadding(25, 15, 25, 15)
//            checkbox.setTextColor(ContextCompat.getColor(requireContext(), R.color.font))
//            checkbox.background = ContextCompat.getDrawable(requireContext(),
//                R.drawable.radio_border
//            )
//            checkbox.buttonDrawable = null
//            checkbox.text = tag.name.toString()
//            checkbox.textSize = 14f
//
////            if(clothingItem.tags.contains(tag.id.toInt())){
////                checkbox.isChecked = true
////            }
//
//            tagsLinearLayout.addView(checkbox)
//        }
//
//    }

//    private fun saveClothing(clothingItem: ClothingItem){
//        val title = titleEditText.text.toString()
//        val tags = mutableListOf<Int>()
//        val checkedRadio = itemTypeRadioGroup.checkedRadioButtonId
//
//        if(title.isEmpty()){
//            Toast.makeText(requireContext(), "Invalid title", Toast.LENGTH_SHORT).show()
//            return
//        }
//        if(checkedRadio == -1){
//            Toast.makeText(requireContext(), "Select item type", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        for (i in 0 until tagsLinearLayout.childCount) {
//            val view = tagsLinearLayout.getChildAt(i)
//            if (view is CheckBox && view.isChecked) {
//                val tag = ClothingTagsManager.getTags()[i]
//                tags.add(tag.id.toInt())
//            }
//        }
//
//        val newClothingItem = ClothingItem(
//            id = clothingItem.id,
//            imageName = clothingItem.imageName,
//            name = title,
//            type = "",
//            categories = listOf("")
//        )
//
//        ClothingItemsManager.editClothingItem(newClothingItem)
//        val fragmentManager = requireActivity().supportFragmentManager
//        fragmentManager.beginTransaction().apply {
//            replace(R.id.fragmentFrame, WardrobeFragment())
//            commit()
//        }
//        Toast.makeText(requireContext(), "Clothing item updated", Toast.LENGTH_SHORT).show()
//
//        val clothingItemList = ClothingItemsManager.getClothingItems()
//
//        saveToFile(clothingItemList)
//
//    }

    private fun removeClothing(item: ClothingItem){
        val itemExists = ClothingItemsManager.clothingItemExists(item)
        if(itemExists){
            ClothingItemsManager.removeClothingItem(item)

            val clothingItems = ClothingItemsManager.getClothingItems()

            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.beginTransaction().apply {
                replace(R.id.fragmentFrame, WardrobeFragment())
                commit()
            }
            Toast.makeText(requireContext(), "Clothing item removed", Toast.LENGTH_SHORT).show()


            saveToFile(clothingItems)
        }else{
            Toast.makeText(requireContext(), "Piece doesn't exist", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveToFile(items: List<ClothingItem>){
        GlobalScope.launch(Dispatchers.Main) {
            val dataResult = ClothingItemsManager.saveClothingItems(requireContext(), items)
            if(dataResult){
                Log.d("ClothingItemDetailsFragment", "Clothing item file saved")
            } else {
                Toast.makeText(requireContext(), "Failed to save item", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun goBack(){
        val fragmentManager = requireActivity().supportFragmentManager
        fragmentManager.popBackStack()
    }


}