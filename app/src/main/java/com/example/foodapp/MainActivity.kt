package com.example.foodapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.foodapp.data.local.AppDatabase
import com.example.foodapp.data.remote.RetrofitClient
import com.example.foodapp.data.repository.RecipeRepository
import com.example.foodapp.ui.RecipeViewModel
import com.example.foodapp.ui.RecipeViewModelFactory
import com.example.foodapp.ui.UiState
import com.example.foodapp.ui.screens.LoadingScreen
import com.example.foodapp.ui.screens.RecipeDetailScreen
import com.example.foodapp.ui.screens.RecipeListScreen
import com.example.foodapp.ui.theme.FoodAppTheme

/**
 * Point d'entrée principal de l'application.
 * Cette classe hérite de ComponentActivity, qui est la base pour les activités utilisant Jetpack Compose.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialisation de la base de données locale (Room)
        val database = AppDatabase.getDatabase(this)
        
        // Initialisation du dépôt (Repository) qui gère les données (API + Base de données)
        val repository = RecipeRepository(RetrofitClient.apiService, database.recipeDao())
        
        // Création d'une fabrique pour instancier le ViewModel avec ses dépendances
        val viewModelFactory = RecipeViewModelFactory(repository)

        // Active l'affichage plein écran (bord à bord)
        enableEdgeToEdge()
        
        // Définit le contenu de l'activité en utilisant Jetpack Compose
        setContent {
            // Applique le thème personnalisé de l'application
            FoodAppTheme {
                // Récupération du ViewModel lié au cycle de vie de l'activité
                val viewModel: RecipeViewModel = viewModel(factory = viewModelFactory)
                
                // Observation de l'état de l'interface utilisateur (UiState)
                // collectAsState convertit le Flow du ViewModel en un State lisible par Compose
                val uiState by viewModel.uiState.collectAsState()
                
                // Lance la navigation de l'application
                AppNavigation(viewModel, uiState)
            }
        }
    }
}

/**
 * Gère la navigation entre les différents écrans de l'application.
 * @param viewModel Le ViewModel partagé pour la logique métier.
 * @param uiState L'état actuel de l'interface utilisateur.
 */
@Composable
fun AppNavigation(viewModel: RecipeViewModel, uiState: UiState<Unit>) {
    // Création du contrôleur de navigation
    val navController = rememberNavController()

    // Configuration de l'hôte de navigation (NavHost)
    // startDestination définit l'écran qui s'affiche au lancement
    NavHost(navController = navController, startDestination = "loading") {
        
        // Écran de chargement (Splash screen / Initialisation)
        composable("loading") {
            LoadingScreen(onLoadingFinished = {
                // Une fois le chargement fini, on va vers la liste et on retire l'écran de chargement de l'historique
                navController.navigate("list") {
                    popUpTo("loading") { inclusive = true }
                }
            })
        }
        
        // Écran de la liste des recettes
        composable("list") {
            RecipeListScreen(
                viewModel = viewModel,
                uiState = uiState,
                onRecipeClick = { recipeId ->
                    // Navigation vers les détails d'une recette spécifique
                    navController.navigate("detail/$recipeId")
                }
            )
        }
        
        // Écran des détails d'une recette
        // {recipeId} est un paramètre dynamique passé dans l'URL de navigation
        composable("detail/{recipeId}") { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getString("recipeId") ?: ""
            RecipeDetailScreen(
                recipeId = recipeId,
                viewModel = viewModel,
                onBack = { 
                    // Retour à l'écran précédent
                    navController.popBackStack() 
                }
            )
        }
    }
}
