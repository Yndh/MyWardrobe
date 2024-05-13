package com.example.mywardrobe.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.example.mywardrobe.R
import com.example.mywardrobe.managers.ClothingTagsManager
import com.example.mywardrobe.managers.Tag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class NewClothingTagFragment : Fragment() {

    private lateinit var goBackImageButton: ImageButton
    private lateinit var tagNameEditText: EditText
    private lateinit var addTagButton: AppCompatButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_clothing_tag, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        goBackImageButton = view.findViewById(R.id.goBackImageButton)
        tagNameEditText = view.findViewById(R.id.tagNameEditText)
        addTagButton = view.findViewById(R.id.addTagButton)

        addTagButton.setOnClickListener {
            addNewTag()
        }

        goBackImageButton.setOnClickListener {
            goBack()
        }
    }

    private fun goBack(){
        val fragmentManager = requireActivity().supportFragmentManager
        fragmentManager.popBackStack()
    }

    private fun addNewTag(){
        val name = tagNameEditText.text.toString()

        if(name.isEmpty()){
            Toast.makeText(requireContext(), "Invalid tag name", Toast.LENGTH_SHORT).show()
            return
        }

        val tag = Tag(
            id = ClothingTagsManager.generateId(),
            name = name
        )

        ClothingTagsManager.addTag(tag)
        val tags = ClothingTagsManager.getTags()

        val fragmentManager = requireActivity().supportFragmentManager
        fragmentManager.beginTransaction().apply {
            replace(R.id.fragmentFrame, WardrobeFragment())
            commit()
        }

        GlobalScope.launch(Dispatchers.Main) {
            val tagResult = ClothingTagsManager.saveClothingTags(requireContext(), tags)
            if(tagResult){
                Log.d("NewClothingFragment", "Tags file saved")
            } else {
                Toast.makeText(requireContext(), "Failed to save tags", Toast.LENGTH_SHORT).show()
            }
        }
    }
}