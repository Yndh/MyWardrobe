package com.example.mywardrobe

object ClothingTypesManager {
    private val itemTypes = mutableListOf("Headwear", "Tops", "Bottoms", "Footwear")

    fun getTypes(): List<String>{
        return itemTypes
    }
    fun getTypeName(index: Int): String{
        if(index > itemTypes.size+1 || index < 1){
            return "Invalid index"
        }

        return itemTypes[index.toInt()-1]
    }
}