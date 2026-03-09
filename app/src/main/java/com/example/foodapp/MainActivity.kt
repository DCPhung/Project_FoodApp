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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val database = AppDatabase.getDatabase(this)
        val repository = RecipeRepository(RetrofitClient.apiService, database.recipeDao())
        val viewModelFactory = RecipeViewModelFactory(repository)

        enableEdgeToEdge()
        setContent {
            FoodAppTheme {
                val viewModel: RecipeViewModel = viewModel(factory = viewModelFactory)
                val uiState by viewModel.uiState.collectAsState()
                
                AppNavigation(viewModel, uiState)
            }
        }
    }
}

@Composable
fun AppNavigation(viewModel: RecipeViewModel, uiState: UiState<Unit>) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "loading") {
        composable("loading") {
            LoadingScreen(onLoadingFinished = {
                navController.navigate("list") {
                    popUpTo("loading") { inclusive = true }
                }
            })
        }
        composable("list") {
            RecipeListScreen(
                viewModel = viewModel,
                uiState = uiState,
                onRecipeClick = { recipeId ->
                    navController.navigate("detail/$recipeId")
                }
            )
        }
        composable("detail/{recipeId}") { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getString("recipeId") ?: ""
            RecipeDetailScreen(
                recipeId = recipeId,
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
