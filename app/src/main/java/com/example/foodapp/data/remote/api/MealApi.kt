package com.example.foodapp.data.remote.api

import com.example.foodapp.data.remote.dto.CategoryResponseDto
import com.example.foodapp.data.remote.dto.RecipeResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interface Retrofit pour l'API TheMealDB.
 */
interface MealApi {
    @GET("search.php")
    suspend fun searchRecipes(@Query("s") query: String): RecipeResponseDto

    @GET("lookup.php")
    suspend fun getRecipeById(@Query("i") id: String): RecipeResponseDto

    @GET("categories.php")
    suspend fun getCategories(): CategoryResponseDto

    @GET("filter.php")
    suspend fun filterByCategory(@Query("c") category: String): RecipeResponseDto
}
