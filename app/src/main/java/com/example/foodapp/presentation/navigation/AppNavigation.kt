package com.example.foodapp.presentation.navigation // Paquetage gérant la navigation de l'application

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.foodapp.presentation.RecipeViewModel
import com.example.foodapp.presentation.screens.detail.RecipeDetailScreen
import com.example.foodapp.presentation.screens.home.RecipeListScreen
import com.example.foodapp.presentation.screens.liked.LikedRecipesScreen
import com.example.foodapp.presentation.screens.recent.RecentRecipesScreen
import com.example.foodapp.presentation.screens.splash.LoadingScreen
import com.example.foodapp.presentation.theme.DeepBlack
import com.example.foodapp.presentation.theme.LightLavender

// Classe scellée définissant les écrans de la barre de navigation
sealed class Screen(val route: String, val icon: @Composable () -> Unit, val label: String) {
    object Home : Screen("list", { Icon(Icons.Default.Home, contentDescription = null) }, "Home")
    object Liked : Screen("liked", { Icon(Icons.Default.Favorite, contentDescription = null) }, "Liked")
    object Recent : Screen("recent", { Icon(Icons.Default.Search, contentDescription = null) }, "Recent")
}

@Composable
fun AppNavigation(viewModel: RecipeViewModel) {
    val navController = rememberNavController() // Initialisation du contrôleur de navigation
    val uiState by viewModel.uiState.collectAsState() // Observation de l'état global
    val navBackStackEntry by navController.currentBackStackEntryAsState() // Récupération de l'entrée actuelle
    val currentDestination = navBackStackEntry?.destination // Destination actuelle

    // Liste des écrans de la barre du bas
    val items = listOf(
        Screen.Home,
        Screen.Liked,
        Screen.Recent
    )

    Scaffold(
        bottomBar = {
            // La barre est maintenant STATIQUE : elle s'affiche dès qu'on est sur un écran principal
            // sans attendre la fin du chargement des données (Success).
            val isMainScreen = currentDestination?.route in listOf("list", "liked", "recent")

            if (isMainScreen) {
                NavigationBar(
                    containerColor = LightLavender,
                    contentColor = DeepBlack
                ) {
                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = screen.icon,
                            label = { Text(screen.label) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = DeepBlack,
                                selectedTextColor = DeepBlack,
                                unselectedIconColor = DeepBlack.copy(alpha = 0.6f),
                                unselectedTextColor = DeepBlack.copy(alpha = 0.6f),
                                indicatorColor = DeepBlack.copy(alpha = 0.1f)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "loading",
            modifier = Modifier.padding(innerPadding)
        ) {
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

            composable("liked") {
                LikedRecipesScreen(
                    viewModel = viewModel,
                    onRecipeClick = { recipeId ->
                        navController.navigate("detail/$recipeId")
                    }
                )
            }

            composable("recent") {
                RecentRecipesScreen(
                    viewModel = viewModel,
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
}
