package com.example.foodapp.data.repository

import com.example.foodapp.data.local.dao.RecipeDao
import com.example.foodapp.data.local.entity.RecipeEntity
import com.example.foodapp.data.remote.api.MealApi
import com.example.foodapp.data.remote.dto.toDomain
import com.example.foodapp.domain.model.Category
import com.example.foodapp.domain.model.Recipe
import com.example.foodapp.domain.model.Ingredient
import com.example.foodapp.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implémentation du repository utilisant la source locale (Room) et distante (Retrofit).
 */
class RecipeRepositoryImpl(
    private val mealApi: MealApi,
    private val recipeDao: RecipeDao
) : RecipeRepository {

    override fun getAllRecipes(): Flow<List<Recipe>> = 
        recipeDao.getAllRecipes().map { it.map { entity -> entity.toDomain() } }

    override suspend fun refreshRecipes(query: String) {
        val response = mealApi.searchRecipes(query)
        response.meals?.map { it.toDomain().toEntity() }?.let {
            recipeDao.insertRecipes(it)
        }
    }

    override suspend fun getCategories(): List<Category> =
        mealApi.getCategories().categories.map { it.toDomain() }

    override suspend fun filterByCategory(category: String) {
        val response = mealApi.filterByCategory(category)
        response.meals?.map { it.toDomain().copy(category = category).toEntity() }?.let {
            recipeDao.insertRecipes(it)
        }
    }

    override fun searchLocal(query: String): Flow<List<Recipe>> =
        recipeDao.searchRecipes(query).map { it.map { entity -> entity.toDomain() } }

    override fun getRecipesByCategoryLocal(category: String): Flow<List<Recipe>> =
        recipeDao.getRecipesByCategory(category).map { it.map { entity -> entity.toDomain() } }

    override suspend fun getRecipeById(id: String): Recipe? {
        val entity = recipeDao.getRecipeById(id)
        if (entity?.strInstructions != null) return entity.toDomain()

        return try {
            val response = mealApi.getRecipeById(id)
            val domain = response.meals?.firstOrNull()?.toDomain()
            domain?.let { 
                recipeDao.insertRecipe(it.toEntity())
                it
            } ?: entity?.toDomain()
        } catch (e: Exception) {
            entity?.toDomain()
        }
    }
}

/**
 * Mappers internes pour la conversion entre Entity et Domain.
 */
fun RecipeEntity.toDomain() = Recipe(
    id = idMeal,
    name = strMeal,
    category = strCategory,
    area = strArea,
    instructions = strInstructions,
    thumbnailUrl = strMealThumb,
    ingredients = ingredients
)

fun Recipe.toEntity() = RecipeEntity(
    idMeal = id,
    strMeal = name,
    strCategory = category,
    strArea = area,
    strInstructions = instructions,
    strMealThumb = thumbnailUrl,
    ingredients = ingredients
)
