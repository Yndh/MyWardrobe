package com.example.mywardrobe

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import androidx.core.view.setPadding

class WardrobeFragment : Fragment() {

    lateinit var newClothingButton: ImageButton
    lateinit var wardobeLinearLayout: LinearLayout

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

        newClothingButton.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentFrame, NewClothingFragment())
                .addToBackStack(null)
                .commit()


        }

        val clothingItems = ClothingItemsManager.getClothingItems()
        displayClothingItems(clothingItems)
    }

    fun displayClothingItems(clothingItems: List<ClothingItem>){
        val inflater = LayoutInflater.from(requireContext())

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
            imageView.setImageBitmap(ClothingItemsManager.getImage(requireContext(), item.imageName))
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP

            val innerLinearLayout = LinearLayout(requireContext())
            val innerLayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            ).also { innerLinearLayout.layoutParams = it }
            innerLayoutParams.setMargins(70, 0, 0 ,0)
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

            for(tag in item.tags){
                val itemTagTextView = TextView(requireContext())
                itemTagTextView.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                itemTagTextView.textSize = 16f
                itemTagTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.font))
                itemTagTextView.text = tag
                itemTagTextView.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_border)
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