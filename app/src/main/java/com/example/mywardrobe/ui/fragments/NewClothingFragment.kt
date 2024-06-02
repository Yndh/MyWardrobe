package com.example.mywardrobe.ui.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.setPadding
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.view.ViewGroup
import android.widget.GridView
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.mywardrobe.R
import com.example.mywardrobe.adapters.SelectCategoriesAdapter
import com.example.mywardrobe.adapters.SelectTagsAdapter
import com.example.mywardrobe.managers.ClothingCategoriesManager
import com.example.mywardrobe.managers.ClothingItem
import com.example.mywardrobe.managers.ClothingItemsManager
import com.example.mywardrobe.managers.ClothingTagsManager
import com.example.mywardrobe.managers.Tag
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.InputStream


class NewClothingFragment : Fragment() {

    private lateinit var clothingImage: ImageView
    private lateinit var addImage: LinearLayout
    private lateinit var clothingImageLayout: LinearLayout
    private lateinit var selectCategories: LinearLayout
    private lateinit var addClothingButton: AppCompatButton
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var pickedImageUri: Uri
    private lateinit var goBackImageButton: ImageButton
    private lateinit var categoriesHorizontalScrollView: HorizontalScrollView
    private lateinit var categoriesLinearLayout: LinearLayout

    private lateinit var selectedCategories:  MutableMap<String, MutableList<String>>
    private var selectedCategoriesType: String? = null
    private lateinit var selectedTags: MutableList<Tag>

    private var bottomSheetDialog: BottomSheetDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_clothing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        clothingImage = view.findViewById(R.id.clothingImage)
        addImage = view.findViewById(R.id.addImage)
        clothingImageLayout = view.findViewById(R.id.clothingImageLayout)
        selectCategories = view.findViewById(R.id.selectCategories)
        addClothingButton = view.findViewById(R.id.addClothingButton)
        goBackImageButton = view.findViewById(R.id.goBackImageButton)
        categoriesHorizontalScrollView = view.findViewById(R.id.categoriesHorizontalScrollView)
        categoriesLinearLayout = view.findViewById(R.id.categoriesLinearLayout)

        selectedCategories = mutableMapOf()
        selectedTags = mutableListOf()


        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                Log.d("NewClothingFragment", "Selected URI: $uri")
                clothingImage.setImageURI(uri)
                clothingImage.setPadding(0)
                clothingImage.scaleType = ImageView.ScaleType.CENTER_INSIDE
                pickedImageUri = uri
                clothingImageLayout.visibility = View.VISIBLE
                addImage.visibility = View.GONE
            } else {
                Log.d("NewClothingFragment", "No media selected")
            }
        }

        addImage.setOnClickListener {
            chooseImage()
        }

        selectCategories.setOnClickListener {
            openModal()
        }

        addClothingButton.setOnClickListener {
            addNewClothing()
        }

        goBackImageButton.setOnClickListener {
            goBack()
        }
    }

    private fun openModal(){
        if(bottomSheetDialog != null && bottomSheetDialog!!.isShowing){
            return
        }

        var gridCategories = true

        val view: View = layoutInflater.inflate(R.layout.select_categories, null)
        bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog?.setContentView(view)
        bottomSheetDialog?.show()

        val setCategoriesAppCompactButton: AppCompatButton = view.findViewById(R.id.setCategoriesAppCompactButton)
        val setTagsAppCompactButton: AppCompatButton = view.findViewById(R.id.setTagsAppCompactButton)

        setCategoriesAppCompactButton.setOnClickListener {
            gridCategories = true
            displayModalGrids(view, gridCategories)
            setCategoriesAppCompactButton.setTypeface(null, Typeface.BOLD)
            setTagsAppCompactButton.setTypeface(null, Typeface.NORMAL)
        }
        setTagsAppCompactButton.setOnClickListener {
            gridCategories = false
            displayModalGrids(view, gridCategories)
            setCategoriesAppCompactButton.setTypeface(null, Typeface.NORMAL)
            setTagsAppCompactButton.setTypeface(null, Typeface.BOLD)
        }

        displayModalGrids(view, gridCategories)

        val close: ImageButton = view.findViewById(R.id.closeDialog)
        close.setOnClickListener {
            bottomSheetDialog?.dismiss()
        }


        val saveCategories: AppCompatButton = view.findViewById(R.id.saveCategories)
        saveCategories.setOnClickListener {
            displayCategories()
            bottomSheetDialog?.dismiss()
        }
    }

    private fun displayModalGrids(view: View, gridCategories: Boolean){
        val categoriesGrid: GridView = view.findViewById(R.id.categoriesGrid)
        val tagsGrid: GridView = view.findViewById(R.id.tagsGrid)
        val noTagsLinearLayout: LinearLayout = view.findViewById(R.id.noTagsLinearLayout)
        if(gridCategories) {
            tagsGrid.visibility = View.GONE
            categoriesGrid.visibility = View.VISIBLE
            noTagsLinearLayout.visibility = View.GONE
            val categories = ClothingCategoriesManager.getCategories()
            categoriesGrid.adapter =
                SelectCategoriesAdapter(requireContext(), categories, selectedCategories, selectedCategoriesType)

        }else{
            tagsGrid.visibility = View.VISIBLE
            categoriesGrid.visibility = View.GONE
            val tags = ClothingTagsManager.getTags()

            if(tags.isNotEmpty()){
                tagsGrid.adapter = SelectTagsAdapter(requireContext(), tags, selectedTags)
                return
            }

            tagsGrid.visibility = View.GONE
            noTagsLinearLayout.visibility = View.VISIBLE
        }
    }

    private fun displayCategories(){
        categoriesLinearLayout.removeAllViews()

        val inflater = LayoutInflater.from(categoriesLinearLayout.context)

        if(selectedCategories.isNotEmpty()){
            selectCategories.visibility = View.GONE
            categoriesHorizontalScrollView.visibility = View.VISIBLE
            selectedCategoriesType = selectedCategories.values.firstOrNull()?.firstOrNull()
         }else{
            selectCategories.visibility = View.VISIBLE
            categoriesHorizontalScrollView.visibility = View.GONE
            selectedCategoriesType = null
        }

        for(category in selectedCategories){
            val categoryView = inflater.inflate(R.layout.grid_item_category, categoriesLinearLayout, false)
            val categoryTextView: TextView = categoryView.findViewById(R.id.categoryTextView)
            categoryTextView.text = category.key

            categoryView.setOnClickListener {
                selectedCategories.clear()
                displayCategories()
            }

            categoriesLinearLayout.addView(categoryView)
        }

        val openModalButton = inflater.inflate(R.layout.add_category_image_button, categoriesLinearLayout, false)
        openModalButton.setOnClickListener { openModal() }
        categoriesLinearLayout.addView(openModalButton)
    }

    private fun goBack(){
        val fragmentManager = requireActivity().supportFragmentManager
        fragmentManager.popBackStack()
    }

    private fun chooseImage(){
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    fun convertImageToByteArray(context: Context, imageUri: Uri): ByteArray? {
        var inputStream: InputStream? = null
        var byteArrayOutputStream: ByteArrayOutputStream? = null
        try {
            inputStream = context.contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            return byteArrayOutputStream.toByteArray()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            inputStream?.close()
            byteArrayOutputStream?.close()
        }
        return null
    }

    private fun addNewClothing(){
        if(!::pickedImageUri.isInitialized){
            Toast.makeText(requireContext(), "Please select an image", Toast.LENGTH_SHORT).show()
            return
        }
        if(selectedCategories.isEmpty()){
            Toast.makeText(requireContext(), "Select category", Toast.LENGTH_SHORT).show()
            return
        }
        if(selectedCategoriesType.isNullOrEmpty()){
            Toast.makeText(requireContext(), "Select category", Toast.LENGTH_SHORT).show()
            return
        }


        val imageByteArray = convertImageToByteArray(requireContext(), pickedImageUri)
        val imageName = "${System.currentTimeMillis()}.png"

        val newClothingItem = ClothingItem(
            id = ClothingItemsManager.generateId(),
            imageName = imageName,
            tags = listOf(),
            type = selectedCategoriesType as String,
            categories = listOf(selectedCategories.keys.first())
        )

        val context: Context = requireContext()

        ClothingItemsManager.addClothingItem(newClothingItem)
        val fragmentManager = requireActivity().supportFragmentManager
        fragmentManager.beginTransaction().apply {
            replace(R.id.fragmentFrame, WardrobeFragment())
            commit()
        }
        Toast.makeText(requireContext(), "Piece added successfully", Toast.LENGTH_SHORT).show()

        val clothingItemList = ClothingItemsManager.getClothingItems()

        GlobalScope.launch(Dispatchers.Main) {
            val imageResult = ClothingItemsManager.saveImage(context, imageName, imageByteArray)
            if(imageResult){
                Log.d("NewClothingFragment", "Image file saved")
            } else {
                Toast.makeText(requireContext(), "Failed to save image", Toast.LENGTH_SHORT).show()
            }

            val dataResult = ClothingItemsManager.saveClothingItems(context, clothingItemList)
            if(dataResult){
                Log.d("NewClothingFragment", "Clothing item file saved")
            } else {
                Toast.makeText(requireContext(), "Failed to save item", Toast.LENGTH_SHORT).show()
            }
        }
    }
}