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

        if(title.isEmpty()){
            Toast.makeText(requireContext(), "Invalid title", Toast.LENGTH_SHORT).show()
            return
        }
        if(!::pickedImageUri.isInitialized){
            Toast.makeText(requireContext(), "Please select an image", Toast.LENGTH_SHORT).show()
            return
        }

        val imageByteArray = convertImageToByteArray(requireContext(), pickedImageUri)

        val newClothingItem = ClothingItem(
            image = imageByteArray ?: byteArrayOf(),
            name = title,
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
            val result = saveClothingItems(requireContext(), clothingItemList)
            if(result){
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

}