package com.example.foodapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.foodapp.domain.model.Ingredient

/**
 * Modèle de la base de données Room.
 */
@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey val idMeal: String,
    val strMeal: String,
    val strCategory: String?,
    val strArea: String?,
    val strInstructions: String?,
    val strMealThumb: String?,
    val ingredients: List<Ingredient>? = null
)
