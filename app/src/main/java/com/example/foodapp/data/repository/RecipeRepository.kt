package com.example.foodapp.data.repository

import com.example.foodapp.data.local.RecipeDao
import com.example.foodapp.data.model.Category
import com.example.foodapp.data.model.Recipe
import com.example.foodapp.data.model.toRecipe
import com.example.foodapp.data.remote.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

/**
 * Le Repository (Dépôt) est le médiateur entre les sources de données (API et Base de données locale).
 */
class RecipeRepository(
    private val apiService: ApiService,
    private val recipeDao: RecipeDao
) {
    /**
     * Expose toutes les recettes de la base de données locale.
     */
    val allRecipes: Flow<List<Recipe>> = recipeDao.getAllRecipes()

    /**
     * Récupère de nouvelles recettes depuis l'API et les enregistre en local.
     * @param query Terme de recherche (par défaut "a" pour avoir une liste initiale).
     */
    suspend fun refreshRecipes(query: String = "a") {
        try {
            val response = apiService.searchRecipes(query)
            // Convertit les modèles API en modèles locaux et les insère en base
            response.meals?.map { it.toRecipe() }?.let { recipes ->
                recipeDao.insertRecipes(recipes)
            }
        } catch (e: Exception) {
            // Si le réseau échoue et qu'on n'a absolument rien en local, on propage l'erreur
            if (recipeDao.getAllRecipes().first().isEmpty()) {
                throw e
            }
        }
    }

    /**
     * Récupère la liste des catégories depuis l'API.
     */
    suspend fun getCategories(): List<Category> {
        return try {
            apiService.getCategories().categories
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Télécharge les recettes d'une catégorie et les stocke en local.
     */
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

    /**
     * Recherche locale dans la base de données.
     */
    fun searchLocal(query: String): Flow<List<Recipe>> {
        return recipeDao.searchRecipes(query)
    }

    /**
     * Filtre local par catégorie.
     */
    fun getRecipesByCategoryLocal(category: String): Flow<List<Recipe>> {
        return recipeDao.getRecipesByCategory(category)
    }

    /**
     * Récupère une recette par son ID.
     * Stratégie : cherche en local d'abord, si incomplet, va chercher sur le web.
     */
    suspend fun getRecipeById(id: String): Recipe? {
        // Tentative de récupération locale
        val local = recipeDao.getRecipeById(id)
        // Si on a déjà les instructions, pas besoin d'aller sur le réseau
        if (local?.strInstructions != null) return local

        // Sinon, on interroge l'API
        return try {
            val response = apiService.getRecipeById(id)
            val remote = response.meals?.firstOrNull()?.toRecipe()
            if (remote != null) {
                // On met à jour la base locale avec les détails complets
                recipeDao.insertRecipe(remote)
                remote
            } else local
        } catch (e: Exception) {
            local
        }
    }
}
