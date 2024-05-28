package com.example.mywardrobe.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mywardrobe.R
import com.example.mywardrobe.adapters.ClothingItemAdapter
import com.example.mywardrobe.adapters.OutfitsAdapter
import com.example.mywardrobe.managers.ClothingCategoriesManager
import com.example.mywardrobe.managers.ClothingItemsManager
import com.example.mywardrobe.managers.Outfit
import com.example.mywardrobe.managers.OutfitManager
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
        Toast.makeText(requireContext(), "${outfitsRecyclerView.height}", Toast.LENGTH_SHORT).show()

        noOutfitsTextView.visibility = View.GONE
        outfitsRecyclerView.visibility = View.VISIBLE
        outfitsAdapter.updateItems(outfits)
    }

    private fun displayDetails(outfit: Outfit){
        if (bottomSheetDialog != null && bottomSheetDialog!!.isShowing) {
            return
        }
    }


}