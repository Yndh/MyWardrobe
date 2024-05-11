package com.example.mywardrobe

object ClothingTypesManager {
    private val itemTypes = arrayOf("Headwear", "Tops", "Bottoms", "Footwear")

    fun getTypes(): Array<String>{
        return itemTypes
    }
    fun getTypeName(index: Int): String{
        if(index > itemTypes.size+1 || index < 1){
            return "Invalid index"
        }

        return itemTypes[index.toInt()-1]
    }
}