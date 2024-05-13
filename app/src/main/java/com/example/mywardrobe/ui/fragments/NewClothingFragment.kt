package com.example.mywardrobe.ui.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
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
import android.view.Gravity
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.mywardrobe.R
import com.example.mywardrobe.managers.ClothingItem
import com.example.mywardrobe.managers.ClothingItemsManager
import com.example.mywardrobe.managers.ClothingTagsManager
import com.example.mywardrobe.managers.ClothingTypesManager
import com.example.mywardrobe.managers.Tag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.InputStream


class NewClothingFragment : Fragment() {

    private lateinit var addImageButton: ImageButton
    private lateinit var addClothingButton: AppCompatButton
    private lateinit var titleEditText: EditText
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var pickedImageUri: Uri
    private lateinit var goBackImageButton: ImageButton
    private lateinit var itemTypeRadioGroup: RadioGroup
    private lateinit var tagsLinearLayout: LinearLayout
    private lateinit var noTagsLinearLayout: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_clothing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addImageButton = view.findViewById(R.id.addImageButton)
        addClothingButton = view.findViewById(R.id.addClothingButton)
        titleEditText = view.findViewById(R.id.titleEditText)
        goBackImageButton = view.findViewById(R.id.goBackImageButton)
        itemTypeRadioGroup = view.findViewById(R.id.itemTypeRadioGroup)
        tagsLinearLayout = view.findViewById(R.id.tagsLinearLayout)
        noTagsLinearLayout = view.findViewById(R.id.noTagsLinearLayout)

        val types = ClothingTypesManager.getTypes()
        val tags = ClothingTagsManager.getTags()
        displayTagsAndTypes(types, tags)

        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
                addImageButton.setImageURI(uri)
                addImageButton.setPadding(0)
                addImageButton.scaleType = ImageView.ScaleType.CENTER_CROP
                pickedImageUri = uri
            } else {
                Log.d("NewClothingFragment", "No media selected")
            }
        }

        addImageButton.setOnClickListener {
            chooseImage()
        }

        addClothingButton.setOnClickListener {
            addNewClothing()
        }

        goBackImageButton.setOnClickListener {
            goBack()
        }
    }

    private fun displayTagsAndTypes(types: List<String>, tags: List<Tag>){
        val inflater = LayoutInflater.from(requireContext())
        var radioButtonIdCounter = 1

        for(type in types){
            val radioButton = RadioButton(requireContext())
            val radioButtonLayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                120
            ).also { radioButton.layoutParams = it }
            radioButtonLayoutParams.setMargins(0, 0, 20, 0)
            radioButton.setPadding(25, 15, 25, 15)
            radioButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.font))
            radioButton.background = ContextCompat.getDrawable(requireContext(),
                R.drawable.radio_border
            )
            radioButton.buttonDrawable = null
            radioButton.text = type.toString()
            radioButton.textSize = 14f
            radioButton.id = radioButtonIdCounter++

            itemTypeRadioGroup.addView(radioButton)
        }

        if(tags.isEmpty()){
            val textView = TextView(requireContext())
            val textViewLayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                120
            ).also { textView.layoutParams = it }
            textView.setPadding(25,15,25,15)
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.font))
            textView.background = ContextCompat.getDrawable(requireContext(),
                R.drawable.rounded_border
            )
            textView.text = "You don't have any tags"
            textView.textSize = 14f
            textView.gravity = Gravity.CENTER
            textView.textAlignment = View.TEXT_ALIGNMENT_CENTER

            noTagsLinearLayout.addView(textView)
            return
        }
        for(tag in tags){
            val checkbox = CheckBox(requireContext())
            val checkboxLayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                120
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

            tagsLinearLayout.addView(checkbox)
        }

    }

    private fun goBack(){
        val fragmentManager = requireActivity().supportFragmentManager
        fragmentManager.popBackStack()
    }

    private fun chooseImage(){
        Log.d("!", "================")
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
        val title = titleEditText.text.toString()
        val tags = mutableListOf<Int>()
        val checkedRadio = itemTypeRadioGroup.checkedRadioButtonId

        if(!::pickedImageUri.isInitialized){
            Toast.makeText(requireContext(), "Please select an image", Toast.LENGTH_SHORT).show()
            return
        }
        if(title.isEmpty()){
            Toast.makeText(requireContext(), "Invalid title", Toast.LENGTH_SHORT).show()
            return
        }
        if(checkedRadio == -1){
            Toast.makeText(requireContext(), "Select item type", Toast.LENGTH_SHORT).show()
            return
        }

        for (i in 0 until tagsLinearLayout.childCount) {
            val view = tagsLinearLayout.getChildAt(i)
            if (view is CheckBox && view.isChecked) {
                val tag = ClothingTagsManager.getTags()[i]
                tags.add(tag.id.toInt())
            }
        }


        val imageByteArray = convertImageToByteArray(requireContext(), pickedImageUri)
        val imageName = "${System.currentTimeMillis()}.png"

        val newClothingItem = ClothingItem(
            id = ClothingItemsManager.generateId(),
            imageName = imageName,
            name = title,
            type = checkedRadio,
            tags = tags
        )

        ClothingItemsManager.addClothingItem(newClothingItem)
        val fragmentManager = requireActivity().supportFragmentManager
        fragmentManager.beginTransaction().apply {
            replace(R.id.fragmentFrame, WardrobeFragment())
            commit()
        }
        Toast.makeText(requireContext(), "Clothing item added successfully", Toast.LENGTH_SHORT).show()

        val clothingItemList = ClothingItemsManager.getClothingItems()

        GlobalScope.launch(Dispatchers.Main) {
            val imageResult = ClothingItemsManager.saveImage(requireContext(), imageName, imageByteArray)
            if(imageResult){
                Log.d("NewClothingFragment", "Image file saved")
            } else {
                Toast.makeText(requireContext(), "Failed to save image", Toast.LENGTH_SHORT).show()
            }

            val dataResult = ClothingItemsManager.saveClothingItems(requireContext(), clothingItemList)
            if(dataResult){
                Log.d("NewClothingFragment", "Clothing item file saved")
            } else {
                Toast.makeText(requireContext(), "Failed to save item", Toast.LENGTH_SHORT).show()
            }
        }
    }
}