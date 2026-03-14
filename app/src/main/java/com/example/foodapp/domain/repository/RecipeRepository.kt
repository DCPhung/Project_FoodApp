package com.example.foodapp.domain.repository

import com.example.foodapp.domain.model.Category
import com.example.foodapp.domain.model.Recipe
import kotlinx.coroutines.flow.Flow

/**
 * Interface du Repository définissant les actions possibles sur les recettes.
 */
interface RecipeRepository {
    fun getAllRecipes(): Flow<List<Recipe>>
    suspend fun refreshRecipes(query: String)
    suspend fun getCategories(): List<Category>
    suspend fun filterByCategory(category: String)
    fun searchLocal(query: String): Flow<List<Recipe>>
    fun getRecipesByCategoryLocal(category: String): Flow<List<Recipe>>
    suspend fun getRecipeById(id: String): Recipe?
}
