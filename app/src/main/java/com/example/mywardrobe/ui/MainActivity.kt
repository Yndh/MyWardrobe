package com.example.mywardrobe.ui


import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.mywardrobe.managers.ClothingItemsManager
import com.example.mywardrobe.managers.ClothingTagsManager
import com.example.mywardrobe.ui.fragments.HomeFragment
import com.example.mywardrobe.R
import com.example.mywardrobe.managers.ClothingCategoriesManager
import com.example.mywardrobe.managers.ClothingItem
import com.example.mywardrobe.managers.OutfitManager
import com.example.mywardrobe.ui.fragments.CreateFragment
import com.example.mywardrobe.ui.fragments.NewClothingFragment
import com.example.mywardrobe.ui.fragments.OutfitsFragment
import com.example.mywardrobe.ui.fragments.WardrobeFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog


class MainActivity : AppCompatActivity() {
    private lateinit var navigation: BottomNavigationView
    private var wardrobeFragment: WardrobeFragment? = null
    private var outfitsFragment: OutfitsFragment? = null

    private var bottomSheetDialog: BottomSheetDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        if (savedInstanceState == null) {
            ClothingItemsManager.loadClothingItems(applicationContext)
            ClothingTagsManager.loadClothingTags(applicationContext)
            ClothingCategoriesManager.loadCategories(applicationContext)
            OutfitManager.loadOutfits(applicationContext)
        }


        val fragmentToOpen = intent.getStringExtra("fragmentToOpen")
        if(fragmentToOpen == "WardrobeFragment"){
            replaceFragment(WardrobeFragment())
        }


        navigation = findViewById(R.id.navigationView)

        navigation.setOnItemSelectedListener { menuItem ->
            when(menuItem.itemId) {
                R.id.action_home -> {
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.action_create -> {
                    replaceFragment(CreateFragment())
                    true
                }
                R.id.action_add -> {
//                    replaceFragment(NewClothingFragment())
                    openModal()
                    true
                }
                R.id.action_wardrobe -> {
                    replaceFragment(WardrobeFragment())
                    true
                }
                R.id.action_outfits -> {
                    replaceFragment(OutfitsFragment())
                    true
                }
                else -> false
            }
        }

        if(savedInstanceState==null){
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentFrame, WardrobeFragment())
                .commit()
        }
    }


    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()

        fragmentTransaction
            .replace(R.id.fragmentFrame, fragment)
            .commit()
    }

    private fun openModal() {
        if (bottomSheetDialog != null && bottomSheetDialog!!.isShowing) {
            return
        }

        val view: View = layoutInflater.inflate(R.layout.add_clothing_dialog, null)
        bottomSheetDialog = BottomSheetDialog(this).apply {
            setContentView(view)
            delegate.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)?.let { bottomSheet ->
                BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        bottomSheetDialog?.show()

        val closeDialog: ImageButton = view.findViewById(R.id.closeDialog)
        val takePhotoLinearLayout: LinearLayout = view.findViewById(R.id.takePhotoLinearLayout)
        val uploadPhotoLinearLayout: LinearLayout = view.findViewById(R.id.uploadPhotoLinearLayout)

        takePhotoLinearLayout.setOnClickListener {

        }

        uploadPhotoLinearLayout.setOnClickListener {
            bottomSheetDialog?.dismiss()
            val intent = Intent(this, NewClothingItem::class.java)
            startActivity(intent)
        }

        closeDialog.setOnClickListener {
            bottomSheetDialog?.dismiss()
        }
    }
}