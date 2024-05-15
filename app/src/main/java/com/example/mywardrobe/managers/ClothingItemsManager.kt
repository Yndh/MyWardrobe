package com.example.mywardrobe.managers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.InputStreamReader

data class ClothingItem(
    val id: Number,
    val imageName: String,
    val name: String,
    val type: Number,
    val tags: List<Int>
)

object ClothingItemsManager {
    private val clothingItems = mutableListOf<ClothingItem>()

    fun generateId(): Number{
        val lastId = clothingItems.lastOrNull()?.id as? Int ?: 0
        return lastId + 1
    }

    fun addClothingItem(item: ClothingItem){
        clothingItems.add(item)
    }

    fun editClothingItem(item: ClothingItem): Boolean {
        val index = clothingItems.indexOfFirst { it.id == item.id }
        return if (index != -1) {
            clothingItems.removeAt(index)
            clothingItems.add(index, item)
            true
        } else {
            false
        }
    }

    fun removeClothingItem(item: ClothingItem){
        clothingItems.removeAll { it == item }
    }

    fun clothingItemExists(item: ClothingItem): Boolean{
        return clothingItems.any { it == item }
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
            Log.d("ClothingItemManager", "data.json doesn't exist. Creating...")
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

    suspend fun saveClothingItems(context: Context, items: List<ClothingItem>): Boolean {
        return withContext(Dispatchers.IO){
            try {
                val jsonString = Gson().toJson(items)
                val fileOutputStream = context.openFileOutput("data.json", Context.MODE_PRIVATE)
                fileOutputStream.write(jsonString.toByteArray())
                fileOutputStream.close()
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    suspend fun saveImage(context: Context, fileName: String, data: ByteArray?): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                data?.let {
                    val fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
                    fileOutputStream.write(data)
                    fileOutputStream.close()
                    true
                } ?: false
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}