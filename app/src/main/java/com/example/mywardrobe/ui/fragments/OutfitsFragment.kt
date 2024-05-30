package com.example.mywardrobe.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mywardrobe.R
import com.example.mywardrobe.adapters.OutfitsAdapter
import com.example.mywardrobe.managers.ClothingItemsManager
import com.example.mywardrobe.managers.Outfit
import com.example.mywardrobe.managers.OutfitManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OutfitsFragment : Fragment() {

    private lateinit var outfitsLinearLayout: LinearLayout
    private lateinit var outfitsRecyclerView: RecyclerView
    private lateinit var noOutfitsTextView: TextView
    private lateinit var outfitsAdapter: OutfitsAdapter

    private var bottomSheetDialog: BottomSheetDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_outfits, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        outfitsLinearLayout = view.findViewById(R.id.outfitsLinearLayout)
        noOutfitsTextView = view.findViewById(R.id.noOutfitsTextView)
        outfitsRecyclerView = view.findViewById(R.id.outfitsRecyclerView)

        outfitsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        outfitsAdapter = OutfitsAdapter(requireContext(), listOf()) { outfit -> displayDetails(outfit) }
        outfitsRecyclerView.adapter = outfitsAdapter

        CoroutineScope(Dispatchers.Main).launch {
            val outfits = withContext(Dispatchers.IO) { OutfitManager.getOutfits() }
            displayOutfits(outfits)
        }
    }

    private fun displayOutfits(outfits: List<Outfit>){
        if (outfits.isEmpty()){
            noOutfitsTextView.visibility = View.VISIBLE
            outfitsRecyclerView.visibility = View.GONE
            return
        }

        noOutfitsTextView.visibility = View.GONE
        outfitsRecyclerView.visibility = View.VISIBLE
        outfitsAdapter.updateItems(outfits)
    }

    private fun displayDetails(outfit: Outfit){
        if (bottomSheetDialog != null && bottomSheetDialog!!.isShowing) {
            return
        }

        val view: View = layoutInflater.inflate(R.layout.outfit_details, null)
        bottomSheetDialog = BottomSheetDialog(requireContext()).apply {
            setContentView(view)
            delegate.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)?.let { bottomSheet ->
                BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        bottomSheetDialog?.show()

        val outfitContainerLinearLayout: LinearLayout = view.findViewById(R.id.outfitContainerLinearLayout)
        val outfitItemsHorizontalScrollView: HorizontalScrollView = view.findViewById(R.id.outfitItemsHorizontalScrollView)
        val outfitItemsLinearLayout: LinearLayout = view.findViewById(R.id.outfitItemsLinearLayout)
        val closeDialog: ImageButton = view.findViewById(R.id.closeDialog)
        val errorTextView: TextView = view.findViewById(R.id.errorTextView)

        val inflater = LayoutInflater.from(outfitItemsLinearLayout.context)
        outfitItemsLinearLayout.removeAllViews()

        if(outfit.items.isNotEmpty()){
            errorTextView.visibility = View.GONE
            outfitItemsHorizontalScrollView.visibility = View.VISIBLE

            for (item in outfit.items){
                // Display Outfit
                val outfitItemImageView = ImageView(requireContext()).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        300,
                        300
                    )
                    scaleType = ScaleType.CENTER_INSIDE
                    setImageBitmap(ClothingItemsManager.getImage(requireContext(), item.imageName))

                }
                outfitContainerLinearLayout.addView(outfitItemImageView)


                // Includes
                val outfitView = inflater.inflate(R.layout.grid_item_category, outfitItemsLinearLayout, false)
                val categoryTextView: TextView = outfitView.findViewById(R.id.categoryTextView)
                val categoryImage: ImageView = outfitView.findViewById(R.id.categoryImage)
                categoryTextView.text = item.type
                categoryImage.setImageBitmap(ClothingItemsManager.getImage(requireContext(), item.imageName))
                outfitItemsLinearLayout.addView(outfitView)
            }
        } else {
            errorTextView.visibility = View.VISIBLE
            outfitItemsHorizontalScrollView.visibility = View.GONE
        }

        closeDialog.setOnClickListener {
            bottomSheetDialog?.dismiss()
        }
    }


}