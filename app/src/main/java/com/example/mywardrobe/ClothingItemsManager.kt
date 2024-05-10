package com.example.mywardrobe

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.InputStreamReader

data class ClothingItem(
    val imageName: String,
    val name: String,
    val tags: List<String>
)

object ClothingItemsManager {
    private val clothingItems = mutableListOf<ClothingItem>()

    fun addClothingItem(item: ClothingItem){
        clothingItems.add(item)
    }

    fun getClothingItems(): List<ClothingItem>{
        return clothingItems.toList()
    }

    fun getImage(context: Context, fileName: String): Bitmap? {
        val file = File(context.filesDir, fileName)
        return if (file.exists()) {
            BitmapFactory.decodeFile(file.absolutePath)
        } else {
            Log.e("ClothingItemManager", "Image not found: $fileName")
            null
        }
    }

    fun loadClothingItems(context: Context){
        val dataFile = File(context.filesDir, "data.json")
        if(!dataFile.exists()){
            Log.d("ClothingItemManager", "Data.json doesn't exist. Creating...")
        }

        try {
            val fileInputStream = FileInputStream(dataFile)
            val inputStreamReader = InputStreamReader(fileInputStream)
            val bufferedReader = BufferedReader(inputStreamReader)

            val jsonString = bufferedReader.use { it.readText() }
            val listType = object : TypeToken<List<ClothingItem>>() {}.type
            val items = Gson().fromJson<List<ClothingItem>>(jsonString, listType)
            clothingItems.clear()
            clothingItems.addAll(items)
        } catch (e: FileNotFoundException) {
            Log.e("ClothingItemsManager", "File not found: ${e.message}")
        } catch (e: Exception) {
            Log.e("ClothingItemsManager", "Error loading data: ${e.message}")
        }
    }
}