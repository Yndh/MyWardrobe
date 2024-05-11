package com.example.mywardrobe

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

        displayTagsAndTypes(ClothingTypesManager.getTypes())

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

    private fun displayTagsAndTypes(types: Array<String>){
        val inflater = LayoutInflater.from(requireContext())

        for(type in types){
            val radioButton = RadioButton(requireContext())
            val radioButtonLayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                120
            ).also { radioButton.layoutParams = it }
            radioButtonLayoutParams.setMargins(0, 0, 20, 0)
            radioButton.setPadding(25, 15, 25, 15)
            radioButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.font))
            radioButton.background = ContextCompat.getDrawable(requireContext(), R.drawable.radio_border)
            radioButton.buttonDrawable = null
            radioButton.text = type.toString()
            radioButton.textSize = 14f

            itemTypeRadioGroup.addView(radioButton)
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
        val tags = listOf("test")
        val checkedRadio = itemTypeRadioGroup.checkedRadioButtonId
        Toast.makeText(requireContext(), "$checkedRadio", Toast.LENGTH_SHORT).show()

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

        val imageByteArray = convertImageToByteArray(requireContext(), pickedImageUri)
        val imageName = "${System.currentTimeMillis()}.png"

        val newClothingItem = ClothingItem(
            imageName = imageName,
            name = title,
            type = checkedRadio+1,
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
            val imageResult = saveImage(requireContext(), imageName, imageByteArray)
            if(imageResult){
                Log.d("NewClothingFragment", "Image file saved")
            } else {
                Toast.makeText(requireContext(), "Failed to save image", Toast.LENGTH_SHORT).show()
            }

            val dataResult = saveClothingItems(requireContext(), clothingItemList)
            if(dataResult){
                Log.d("NewClothingFragment", "Clothing item file saved")
            } else {
                Toast.makeText(requireContext(), "Failed to save item", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun saveClothingItems(context: Context, items: List<ClothingItem>): Boolean {
        return withContext(Dispatchers.IO){
            try {
                val jsonString = Gson().toJson(items)
                val fileOutputStream = context.openFileOutput("data.json", Context.MODE_PRIVATE)
                fileOutputStream.write(jsonString.toByteArray())
                fileOutputStream.close()
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    private suspend fun saveImage(context: Context, fileName: String, data: ByteArray?): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                data?.let {
                    val fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
                    fileOutputStream.write(data)
                    fileOutputStream.close()
                    true
                } ?: false
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

}