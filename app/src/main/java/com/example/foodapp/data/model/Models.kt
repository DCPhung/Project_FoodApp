package com.example.foodapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class RecipeResponse(
    val meals: List<RecipeRemote>?
)

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey val idMeal: String,
    val strMeal: String,
    val strCategory: String?,
    val strArea: String?,
    val strInstructions: String?,
    val strMealThumb: String?,
    val ingredients: List<Ingredient>? = null
)

data class RecipeRemote(
    val idMeal: String,
    val strMeal: String,
    val strCategory: String?,
    val strArea: String?,
    val strInstructions: String?,
    val strMealThumb: String?,
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

data class Ingredient(
    val name: String,
    val measure: String
)

data class CategoryResponse(
    val categories: List<Category>
)

data class Category(
    val idCategory: String,
    val strCategory: String,
    val strCategoryThumb: String
)

fun RecipeRemote.toRecipe(): Recipe {
    val ingredients = mutableListOf<Ingredient>()
    
    fun addIfNotEmpty(name: String?, measure: String?) {
        if (!name.isNullOrBlank()) {
            ingredients.add(Ingredient(name, measure ?: ""))
        }
    }

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
