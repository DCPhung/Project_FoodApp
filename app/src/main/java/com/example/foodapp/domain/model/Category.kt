package com.example.foodapp.domain.model

/**
 * Représente une catégorie de nourriture (ex: Beef, Chicken, Dessert).
 */
data class Category(
    val id: String,
    val name: String,
    val thumbnailUrl: String
)
