package com.example.mywardrobe

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.setPadding

class NewClothingFragment : Fragment() {

    private lateinit var addImageButton: ImageButton
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>


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

        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
                addImageButton.setImageURI(uri)
                addImageButton.setPadding(0)
                addImageButton.scaleType = ImageView.ScaleType.CENTER_CROP
            } else {
                Log.d("NewClothingFragment", "No media selected")
            }
        }

        addImageButton.setOnClickListener {
            chooseImage()
        }
    }

    private fun chooseImage(){
        Log.d("!", "================")
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
}