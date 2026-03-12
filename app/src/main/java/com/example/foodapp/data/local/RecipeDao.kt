package com.example.foodapp.data.local

import androidx.room.*
import com.example.foodapp.data.model.Recipe
import kotlinx.coroutines.flow.Flow

/**
 * Interface DAO (Data Access Object) pour la table des recettes.
 * C'est ici que l'on définit toutes les requêtes SQL pour interagir avec la base de données Room.
 */
@Dao
interface RecipeDao {
    
    /**
     * Récupère toutes les recettes enregistrées.
     * Utilise Flow pour permettre une mise à jour en temps réel de l'interface
     * dès que les données changent en base.
     */
    @Query("SELECT * FROM recipes")
    fun getAllRecipes(): Flow<List<Recipe>>

    /**
     * Recherche des recettes par leur nom.
     * @param query Le texte recherché par l'utilisateur.
     */
    @Query("SELECT * FROM recipes WHERE strMeal LIKE '%' || :query || '%'")
    fun searchRecipes(query: String): Flow<List<Recipe>>

    /**
     * Filtre les recettes par catégorie (ex: "Dessert").
     */
    @Query("SELECT * FROM recipes WHERE strCategory = :category")
    fun getRecipesByCategory(category: String): Flow<List<Recipe>>

    /**
     * Récupère une recette spécifique via son identifiant unique.
     * Cette fonction est "suspend" car elle s'exécute sur un thread secondaire.
     */
    @Query("SELECT * FROM recipes WHERE idMeal = :id")
    suspend fun getRecipeById(id: String): Recipe?

    /**
     * Insère une liste de recettes dans la base.
     * REPLACE : Si une recette existe déjà avec le même ID, elle est mise à jour.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(recipes: List<Recipe>)

    /**
     * Insère ou met à jour une seule recette.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: Recipe)
}
