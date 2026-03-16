package com.example.foodapp.data.repository

import com.example.foodapp.data.local.dao.RecipeDao
import com.example.foodapp.data.local.entity.RecipeEntity
import com.example.foodapp.data.remote.api.MealApi // Utilisation de MealApi
import com.example.foodapp.data.remote.dto.toDomain
import com.example.foodapp.domain.model.Category
import com.example.foodapp.domain.model.Recipe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Le Repository gère la synchronisation entre l'API et la base de données locale.
 */
class RecipeRepository(
    private val mealApi: MealApi, // Utilisation de MealApi
    private val recipeDao: RecipeDao
) {
    val allRecipes: Flow<List<Recipe>> = recipeDao.getAllRecipes().map { entities -> 
        entities.map { it.toDomainModel() } 
    }

    suspend fun refreshRecipes(query: String = "a") {
        try {
            val response = mealApi.searchRecipes(query)
            response.meals?.map { it.toDomain().toEntity() }?.let { entities ->
                recipeDao.insertRecipes(entities)
            }
        } catch (e: Exception) {
            if (recipeDao.getAllRecipes().first().isEmpty()) throw e
        }
    }

    suspend fun getCategories(): List<Category> {
        return try {
            mealApi.getCategories().categories.map { it.toDomain() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun filterByCategory(category: String) {
        try {
            val response = mealApi.filterByCategory(category)
            response.meals?.map { 
                it.toDomain().copy(category = category).toEntity() 
            }?.let { entities ->
                recipeDao.insertRecipes(entities)
            }
        } catch (e: Exception) {
            throw e
        }
    }

    fun searchLocal(query: String): Flow<List<Recipe>> {
        return recipeDao.searchRecipes(query).map { entities -> 
            entities.map { it.toDomainModel() } 
        }
    }

    fun getRecipesByCategoryLocal(category: String): Flow<List<Recipe>> {
        return recipeDao.getRecipesByCategory(category).map { entities -> 
            entities.map { it.toDomainModel() } 
        }
    }

    suspend fun getRecipeById(id: String): Recipe? {
        val local = recipeDao.getRecipeById(id)
        if (local?.strInstructions != null) return local.toDomainModel()

        return try {
            val response = mealApi.getRecipeById(id)
            val remote = response.meals?.firstOrNull()?.toDomain()
            if (remote != null) {
                recipeDao.insertRecipe(remote.toEntity())
                remote
            } else local?.toDomainModel()
        } catch (e: Exception) {
            local?.toDomainModel()
        }
    }

    // Mappers internes pour assurer la cohérence entre les couches
    private fun RecipeEntity.toDomainModel() = Recipe(
        id = idMeal,
        name = strMeal,
        category = strCategory,
        area = strArea,
        instructions = strInstructions,
        thumbnailUrl = strMealThumb,
        ingredients = ingredients
    )

    private fun Recipe.toEntity() = RecipeEntity(
        idMeal = id,
        strMeal = name,
        strCategory = category,
        strArea = area,
        strInstructions = instructions,
        strMealThumb = thumbnailUrl,
        ingredients = ingredients
    )
}
