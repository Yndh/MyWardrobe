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

data class Tag(
    val id: Number,
    val name: String
)

object ClothingTagsManager {
    private val clothingTags = mutableListOf<Tag>()

    fun generateId(): Number{
        return clothingTags.size+1
    }

    fun addTag(tag: Tag) {
        clothingTags.add(tag)
    }

    fun getTags(): List<Tag> {
        return clothingTags
    }

    fun getTagName(id: Number): String? {
        val tag = clothingTags.find { it.id == id }
        return tag?.name
    }


    fun loadClothingTags(context: Context){
        val dataFile = File(context.filesDir, "tags.json")
        if(!dataFile.exists()){
            Log.d("ClothingItemManager", "tags.json doesn't exist. Creating...")
        }

        try {
            val fileInputStream = FileInputStream(dataFile)
            val inputStreamReader = InputStreamReader(fileInputStream)
            val bufferedReader = BufferedReader(inputStreamReader)

            val jsonString = bufferedReader.use { it.readText() }
            val listType = object : TypeToken<List<Tag>>() {}.type
            val tags = Gson().fromJson<List<Tag>>(jsonString, listType)
            clothingTags.clear()
            clothingTags.addAll(tags)
        } catch (e: FileNotFoundException) {
            Log.e("ClothingItemsManager", "File not found: ${e.message}")
        } catch (e: Exception) {
            Log.e("ClothingItemsManager", "Error loading data: ${e.message}")
        }
    }

    suspend fun saveClothingTags(context: Context, tags: List<Tag>): Boolean {
        return withContext(Dispatchers.IO){
            try {
                val jsonString = Gson().toJson(tags)
                val fileOutputStream = context.openFileOutput("tags.json", Context.MODE_PRIVATE)
                fileOutputStream.write(jsonString.toByteArray())
                fileOutputStream.close()
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}