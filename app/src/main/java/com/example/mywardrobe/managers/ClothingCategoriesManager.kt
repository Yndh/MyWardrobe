package com.example.mywardrobe.managers

import android.content.Context
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

object ClothingCategoriesManager {
    private val types = listOf("Headwear", "Tops", "Bottoms", "Footwear", "Accessories")

    private val categories = mutableMapOf(
        "Caps" to listOf("Headwear"),
        "Beanies" to listOf("Headwear"),
        "Headbands" to listOf("Headwear"),
        "T-Shirts" to listOf("Tops"),
        "Shirts" to listOf("Tops"),
        "Tank Tops" to listOf("Tops"),
        "Sweaters" to listOf("Tops"),
        "Hoodies" to listOf("Tops"),
        "Jeans" to listOf("Bottoms"),
        "Trousers" to listOf("Bottoms"),
        "Shorts" to listOf("Bottoms"),
        "Jorts" to listOf("Bottoms"),
        "Boots" to listOf("Footwear"),
        "Shoes" to listOf("Footwear"),
        "Sneakers" to listOf("Footwear"),
        "Bags" to listOf("Accessories"),
        "Belts" to listOf("Accessories"),
        "Jewelry" to listOf("Accessories"),
        "Sunglasses" to listOf("Accessories")
    )

    fun getCategories(): Map<String, List<String>> {
        return categories
    }

    fun getTypes(category: String): List<String> {
        return categories[category] ?: emptyList()
    }

    fun getAllTypes(): List<String> {
        return types
    }

    fun addCategory(category: String, types: List<String>) {
        if(!categories.containsKey(category)){
            categories[category] = types.toMutableList()
        }
    }

    fun removeCategory(category: String){
        categories.remove(category)
    }

    fun addType(category: String, type: String) {
        if (categories.containsKey(category)) {
            if (!categories[category]!!.contains(type)) {
                categories[category] = categories[category]!! + type
            }
        } else {
            categories[category] = listOf(type)
        }
    }

    fun removeType(category: String, type: String) {
        if (categories.containsKey(category)) {
            categories[category] = categories[category]!!.filter { it != type }
        }
    }


    fun loadCategories(context: Context) {
        val dataFile = File(context.filesDir, "categories.json")
        if (dataFile.exists()) {
            try {
                val fileInputStream = FileInputStream(dataFile)
                val inputStreamReader = InputStreamReader(fileInputStream)
                val bufferedReader = BufferedReader(inputStreamReader)

                val jsonString = bufferedReader.use { it.readText() }
                val loadedCategories: Map<String, List<String>> = Gson().fromJson(jsonString, object : TypeToken<Map<String, List<String>>>() {}.type)

                categories.putAll(categories)

            } catch (e: FileNotFoundException) {
                Log.e("ClothingCategoriesManager", "File not found: ${e.message}")
            } catch (e: Exception) {
                Log.e("ClothingCategoriesManager", "Error loading data: ${e.message}")
            }
        } else {
            Log.d("ClothingCategoriesManager", "categories.json doesn't exist.")
        }
    }

    suspend fun saveCategories(context: Context): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val jsonString = Gson().toJson(categories)
                val fileOutputStream = context.openFileOutput("categories.json", Context.MODE_PRIVATE)
                fileOutputStream.write(jsonString.toByteArray())
                fileOutputStream.close()
                true
            } catch (e: Exception) {
                Log.e("ClothingCategoriesManager", "Error saving data: ${e.message}")
                false
            }
        }
    }
}
