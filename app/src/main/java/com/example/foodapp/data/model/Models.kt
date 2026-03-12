package com.example.foodapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * Modèle de réponse pour l'API des recettes (TheMealDB).
 * L'API renvoie un objet contenant une liste de "meals".
 */
data class RecipeResponse(
    val meals: List<RecipeRemote>?
)

/**
 * Modèle de données représentant une Recette dans l'application.
 * L'annotation @Entity indique que cette classe sera une table dans la base de données Room.
 */
@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey val idMeal: String, // Identifiant unique de la recette
    val strMeal: String,            // Nom du plat
    val strCategory: String?,       // Catégorie (ex: Dessert, Seafood)
    val strArea: String?,           // Origine géographique (ex: French, Italian)
    val strInstructions: String?,   // Instructions de préparation
    val strMealThumb: String?,      // URL de l'image de la recette
    val ingredients: List<Ingredient>? = null // Liste simplifiée des ingrédients
)

/**
 * Modèle de données brut reçu de l'API Retrofit.
 * L'API TheMealDB renvoie les ingrédients dans des champs séparés (strIngredient1, strMeasure1, etc.).
 */
data class RecipeRemote(
    val idMeal: String,
    val strMeal: String,
    val strCategory: String?,
    val strArea: String?,
    val strInstructions: String?,
    val strMealThumb: String?,
    // Les ingrédients sont fournis de manière peu pratique par l'API (20 colonnes max)
    val strIngredient1: String?, val strMeasure1: String?,
    val strIngredient2: String?, val strMeasure2: String?,
    val strIngredient3: String?, val strMeasure3: String?,
    val strIngredient4: String?, val strMeasure4: String?,
    val strIngredient5: String?, val strMeasure5: String?,
    val strIngredient6: String?, val strMeasure6: String?,
    val strIngredient7: String?, val strMeasure7: String?,
    val strIngredient8: String?, val strMeasure8: String?,
    val strIngredient9: String?, val strMeasure9: String?,
    val strIngredient10: String?, val strMeasure10: String?,
    val strIngredient11: String?, val strMeasure11: String?,
    val strIngredient12: String?, val strMeasure12: String?,
    val strIngredient13: String?, val strMeasure13: String?,
    val strIngredient14: String?, val strMeasure14: String?,
    val strIngredient15: String?, val strMeasure15: String?,
    val strIngredient16: String?, val strMeasure16: String?,
    val strIngredient17: String?, val strMeasure17: String?,
    val strIngredient18: String?, val strMeasure18: String?,
    val strIngredient19: String?, val strMeasure19: String?,
    val strIngredient20: String?, val strMeasure20: String?
)

/**
 * Représente un ingrédient avec sa quantité.
 */
data class Ingredient(
    val name: String,   // Nom de l'ingrédient (ex: Sucre)
    val measure: String // Quantité (ex: 200g)
)

/**
 * Modèle de réponse pour l'API des catégories.
 */
data class CategoryResponse(
    val categories: List<Category>
)

/**
 * Représente une catégorie de nourriture (ex: Beef, Chicken, Dessert).
 */
data class Category(
    val idCategory: String,
    val strCategory: String,
    val strCategoryThumb: String
)

/**
 * Fonction d'extension pour convertir un modèle API (RecipeRemote) en modèle local (Recipe).
 * Cette fonction regroupe les 20 champs d'ingrédients disparates en une liste propre.
 */
fun RecipeRemote.toRecipe(): Recipe {
    val ingredients = mutableListOf<Ingredient>()
    
    // Fonction utilitaire pour ajouter un ingrédient s'il n'est pas vide
    fun addIfNotEmpty(name: String?, measure: String?) {
        if (!name.isNullOrBlank()) {
            ingredients.add(Ingredient(name, measure ?: ""))
        }
    }

    // On parcourt manuellement les 20 emplacements possibles prévus par l'API
    addIfNotEmpty(strIngredient1, strMeasure1)
    addIfNotEmpty(strIngredient2, strMeasure2)
    addIfNotEmpty(strIngredient3, strMeasure3)
    addIfNotEmpty(strIngredient4, strMeasure4)
    addIfNotEmpty(strIngredient5, strMeasure5)
    addIfNotEmpty(strIngredient6, strMeasure6)
    addIfNotEmpty(strIngredient7, strMeasure7)
    addIfNotEmpty(strIngredient8, strMeasure8)
    addIfNotEmpty(strIngredient9, strMeasure9)
    addIfNotEmpty(strIngredient10, strMeasure10)
    addIfNotEmpty(strIngredient11, strMeasure11)
    addIfNotEmpty(strIngredient12, strMeasure12)
    addIfNotEmpty(strIngredient13, strMeasure13)
    addIfNotEmpty(strIngredient14, strMeasure14)
    addIfNotEmpty(strIngredient15, strMeasure15)
    addIfNotEmpty(strIngredient16, strMeasure16)
    addIfNotEmpty(strIngredient17, strMeasure17)
    addIfNotEmpty(strIngredient18, strMeasure18)
    addIfNotEmpty(strIngredient19, strMeasure19)
    addIfNotEmpty(strIngredient20, strMeasure20)

    return Recipe(
        idMeal = idMeal,
        strMeal = strMeal,
        strCategory = strCategory,
        strArea = strArea,
        strInstructions = strInstructions,
        strMealThumb = strMealThumb,
        ingredients = if (ingredients.isNotEmpty()) ingredients else null
    )
}
