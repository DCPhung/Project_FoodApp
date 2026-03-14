package com.example.foodapp.domain.model

/**
 * Modèle de données métier représentant une Recette.
 */
data class Recipe(
    val id: String,
    val name: String,
    val category: String?,
    val area: String?,
    val instructions: String?,
    val thumbnailUrl: String?,
    val ingredients: List<Ingredient>? = null
)

/**
 * Représente un ingrédient avec sa quantité.
 */
data class Ingredient(
    val name: String,
    val measure: String
)
