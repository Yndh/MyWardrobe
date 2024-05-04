package com.example.mywardrobe

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

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
                    Toast.makeText(this@MainActivity, "Home", Toast.LENGTH_LONG ).show()
                    true
                }
                R.id.action_create -> {
                    Toast.makeText(this@MainActivity, "Create", Toast.LENGTH_LONG ).show()
                    true
                }
                R.id.action_wardrobe -> {
                    Toast.makeText(this@MainActivity, "Wardrobe", Toast.LENGTH_LONG ).show()
                    true
                }
                else -> false
            }
        }
    }



}