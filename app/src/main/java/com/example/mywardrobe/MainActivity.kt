package com.example.mywardrobe


import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    lateinit var navigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
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
                R.id.action_wardrobe -> {
                    replaceFragment(WardrobeFragment())
                    true
                }
                else -> false
            }
        }

        if(savedInstanceState==null){
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentFrame, HomeFragment())
                .commit()
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
//        fragmentTransaction.setCustomAnimations(
//            R.anim.slide_in,
//            R.anim.slide_out
//        )

        fragmentTransaction
            .replace(R.id.fragmentFrame, fragment)
            .commit()
    }
}