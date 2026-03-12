package com.example.foodapp.data.remote

import com.example.foodapp.data.model.CategoryResponse
import com.example.foodapp.data.model.RecipeResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interface Retrofit définissant les appels API vers TheMealDB.
 * Chaque fonction correspond à un endpoint spécifique de l'API.
 */
interface ApiService {

    /**
     * Recherche des recettes par nom.
     * @param query Nom ou partie du nom de la recette (ex: "Arrabiata").
     */
    @GET("search.php")
    suspend fun searchRecipes(@Query("s") query: String): RecipeResponse

    /**
     * Récupère les détails complets d'une recette via son identifiant.
     * @param id L'identifiant unique de la recette (ex: "52771").
     */
    @GET("lookup.php")
    suspend fun getRecipeById(@Query("i") id: String): RecipeResponse

    /**
     * Liste toutes les catégories de nourriture disponibles (Beef, Chicken, etc.).
     */
    @GET("categories.php")
    suspend fun getCategories(): CategoryResponse

    /**
     * Filtre les recettes selon une catégorie spécifique.
     */
    @GET("filter.php")
    suspend fun filterByCategory(@Query("c") category: String): RecipeResponse
}
