package com.example.foodapp.data.remote

import com.example.foodapp.data.model.CategoryResponse
import com.example.foodapp.data.model.RecipeResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("search.php")
    suspend fun searchRecipes(@Query("s") query: String): RecipeResponse

    @GET("lookup.php")
    suspend fun getRecipeById(@Query("i") id: String): RecipeResponse

    @GET("categories.php")
    suspend fun getCategories(): CategoryResponse

    @GET("filter.php")
    suspend fun filterByCategory(@Query("c") category: String): RecipeResponse
}