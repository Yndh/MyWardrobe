package com.example.mywardrobe.ui.fragments

import android.app.ActionBar.LayoutParams
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.example.mywardrobe.R
import com.example.mywardrobe.managers.ClothingItemsManager
import com.example.mywardrobe.managers.Outfit
import com.example.mywardrobe.managers.OutfitManager
import com.google.gson.Gson

class HomeFragment : Fragment() {

    lateinit var outfitsLinearLayout: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        outfitsLinearLayout = view.findViewById(R.id.outfitsLinearLayout)

        var outfits = OutfitManager.getOutfits()

        displayOutfits(outfits)
    }

    private fun displayOutfits(outfits: List<Outfit>) {
        for(outfit in outfits){
            val outerLinearLayout = LinearLayout(requireContext())
            val outerLinearLayoutLayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                400
            ).also { outerLinearLayout.layoutParams = it }
            outerLinearLayoutLayoutParams.setMargins(0, 0, 20, 0)
            outerLinearLayout.setPadding(20, 20, 20, 20)
            outerLinearLayout.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_border)
            outerLinearLayout.orientation = LinearLayout.HORIZONTAL
            outerLinearLayout.gravity = Gravity.CENTER

            for(item in outfit.items){
                val imageView = ImageView(requireContext())
                val imageViewLayoutParams = LinearLayout.LayoutParams(
                    160,
                    160
                ).also { imageView.layoutParams = it }
                imageViewLayoutParams.setMargins(0, 0, 20, 0)
                imageView.scaleType = ImageView.ScaleType.CENTER_CROP
                imageView.setImageBitmap(ClothingItemsManager.getImage(requireContext(), item.imageName))

                outerLinearLayout.addView(imageView)
            }

            outerLinearLayout.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("outfit", Gson().toJson(outfit))
                }

                val createFragment = CreateFragment().apply {
                    arguments = bundle
                }

                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentFrame, createFragment)
                    .addToBackStack(null)
                    .commit()
            }

            outfitsLinearLayout.addView(outerLinearLayout)
        }
    }
}