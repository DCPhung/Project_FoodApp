package com.example.foodapp.data.local

import androidx.room.TypeConverter
import com.example.foodapp.data.model.Ingredient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Classe de conversion pour Room.
 * Room ne sait pas comment stocker des listes d'objets complexes par défaut.
 * Ces fonctions convertissent la liste d'Ingrédients en une chaîne JSON pour le stockage,
 * et inversement lors de la lecture.
 */
class Converters {
    
    /**
     * Convertit une liste d'Ingrédients en String (Format JSON).
     */
    @TypeConverter
    fun fromIngredientList(value: List<Ingredient>?): String? {
        return Gson().toJson(value)
    }

    /**
     * Reconvertit une String (Format JSON) en liste d'Ingrédients.
     */
    @TypeConverter
    fun toIngredientList(value: String?): List<Ingredient>? {
        // Détermine le type de la liste pour que Gson puisse reconstruire les objets correctement
        val listType = object : TypeToken<List<Ingredient>>() {}.type
        return Gson().fromJson(value, listType)
    }
}
