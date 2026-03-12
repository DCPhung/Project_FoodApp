package com.example.foodapp.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Client pour la communication réseau via Retrofit.
 * Utilise le patron Singleton (object) pour garantir qu'on ne crée qu'une seule instance du client.
 */
object RetrofitClient {
    // URL de base de l'API TheMealDB
    private const val BASE_URL = "https://www.themealdb.com/api/json/v1/1/"

    /**
     * Instance paresseuse (lazy) du service API. 
     * Elle ne sera créée que lors de son premier appel.
     */
    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            // Utilise Gson pour convertir automatiquement les réponses JSON de l'API en objets Kotlin
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
