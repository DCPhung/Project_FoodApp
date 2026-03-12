package com.example.foodapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.foodapp.data.model.Recipe

/**
 * Point d'accès principal à la base de données locale du smartphone.
 * @Database définit les tables (entities) et la version.
 * @TypeConverters indique comment convertir les données complexes (comme les listes).
 */
@Database(entities = [Recipe::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    
    /**
     * Permet d'accéder aux fonctions de lecture/écriture des recettes.
     */
    abstract fun recipeDao(): RecipeDao

    companion object {
        // @Volatile assure que la variable est toujours à jour pour tous les threads
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Patron Singleton : On ne crée qu'une seule instance de la base de données
         * pour toute la durée de vie de l'application afin d'éviter les conflits.
         */
        fun getDatabase(context: Context): AppDatabase {
            // Si l'instance existe déjà, on la retourne, sinon on la crée
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "recipe_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
