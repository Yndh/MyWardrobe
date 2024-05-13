package com.example.mywardrobe.managers

object ClothingTypesManager {
    private val itemTypes = mutableListOf("Headwear", "Tops", "Bottoms", "Footwear")

    fun getTypes(): List<String>{
        return itemTypes
    }
    fun getTypeName(index: Int): String{
        if(index > itemTypes.size || index < 0){
            return "Invalid index"
        }

        return itemTypes[index.toInt()-1]
    }
}