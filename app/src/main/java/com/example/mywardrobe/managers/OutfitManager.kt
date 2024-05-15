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


data class Outfit(
    var id: Number,
    var items: List<ClothingItem>
)

object OutfitManager {
    private val outfits = mutableListOf<Outfit>()

    fun generateId(): Number {
        val lastId = outfits.lastOrNull()?.id as? Int ?: 0
        return lastId + 1
    }

    fun addOutfit(outfit: Outfit){
        outfits.add(outfit)
    }

    fun removeOutfit(outfit: Outfit){
        outfits.removeAll { it.items == outfit.items }
    }

    fun outfitExists(outfit: Outfit): Boolean{
        return outfits.any { it.items == outfit.items }
    }

    fun getOutfits(): List<Outfit> {
        return outfits.toList()
    }

    fun loadOutfits(context: Context){
        val dataFile = File(context.filesDir, "outfits.json")
        if(!dataFile.exists()){
            Log.d("OutfitManager", "outfits.json doesn't exist. Creating...")
        }

        try {
            val fileInputStream = FileInputStream(dataFile)
            val inputStreamReader = InputStreamReader(fileInputStream)
            val bufferedReader = BufferedReader(inputStreamReader)

            val jsonString = bufferedReader.use { it.readText() }
            val listType = object : TypeToken<List<Outfit>>() {}.type
            val items = Gson().fromJson<List<Outfit>>(jsonString, listType)
            outfits.clear()
            outfits.addAll(items)
        } catch (e: FileNotFoundException) {
            Log.e("OutfitManager", "File not found: ${e.message}")
        } catch (e: Exception) {
            Log.e("OutfitManager", "Error loading data: ${e.message}")
        }
    }

    suspend fun saveOutfits(context: Context, outfit: List<Outfit>): Boolean {
        return withContext(Dispatchers.IO){
            try {
                val jsonString = Gson().toJson(outfit)
                val fileOutputStream = context.openFileOutput("outfits.json", Context.MODE_PRIVATE)
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