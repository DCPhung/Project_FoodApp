package com.example.foodapp.data.repository

import com.example.foodapp.data.local.RecipeDao
import com.example.foodapp.data.model.Category
import com.example.foodapp.data.model.Recipe
import com.example.foodapp.data.model.toRecipe
import com.example.foodapp.data.remote.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class RecipeRepository(
    private val apiService: ApiService,
    private val recipeDao: RecipeDao
) {
    val allRecipes: Flow<List<Recipe>> = recipeDao.getAllRecipes()

    suspend fun refreshRecipes(query: String = "a") {
        try {
            val response = apiService.searchRecipes(query)
            response.meals?.map { it.toRecipe() }?.let { recipes ->
                recipeDao.insertRecipes(recipes)
            }
        } catch (e: Exception) {
            // Handle error or let ViewModel handle it
            if (recipeDao.getAllRecipes().first().isEmpty()) {
                throw e
            }
        }
    }

    suspend fun getCategories(): List<Category> {
        return try {
            apiService.getCategories().categories
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun filterByCategory(category: String) {
        try {
            val response = apiService.filterByCategory(category)
            response.meals?.map { it.toRecipe() }?.let { recipes ->
                recipeDao.insertRecipes(recipes)
            }
        } catch (e: Exception) {
            throw e
        }
    }

    fun searchLocal(query: String): Flow<List<Recipe>> {
        return recipeDao.searchRecipes(query)
    }

    fun getRecipesByCategoryLocal(category: String): Flow<List<Recipe>> {
        return recipeDao.getRecipesByCategory(category)
    }

    suspend fun getRecipeById(id: String): Recipe? {
        // Try local first
        val local = recipeDao.getRecipeById(id)
        if (local?.strInstructions != null) return local

        // If not found or incomplete, fetch from remote
        return try {
            val response = apiService.getRecipeById(id)
            val remote = response.meals?.firstOrNull()?.toRecipe()
            if (remote != null) {
                recipeDao.insertRecipe(remote)
                remote
            } else local
        } catch (e: Exception) {
            local
        }
    }
}