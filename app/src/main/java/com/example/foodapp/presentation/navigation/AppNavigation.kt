package com.example.foodapp.presentation.navigation // Paquetage gérant la navigation de l'application

import androidx.compose.foundation.layout.padding // Pour ajouter des marges autour des composants
import androidx.compose.material.icons.Icons // Accès à la bibliothèque d'icônes Material
import androidx.compose.material.icons.filled.Favorite // Icône de coeur (favoris)
import androidx.compose.material.icons.filled.Home // Icône de maison (accueil)
import androidx.compose.material.icons.filled.Search // Icône de loupe (récent/recherche)
import androidx.compose.material3.* // Import des composants Material Design 3 (Scaffold, NavigationBar, etc.)
import androidx.compose.runtime.Composable // Annotation pour les fonctions d'interface Compose
import androidx.compose.runtime.collectAsState // Pour convertir les Flow du ViewModel en état de l'UI
import androidx.compose.runtime.getValue // Pour faciliter l'accès aux valeurs des états
import androidx.compose.ui.Modifier // Pour modifier l'apparence ou le comportement des composants
import androidx.navigation.NavDestination.Companion.hierarchy // Pour gérer la hiérarchie des destinations de navigation
import androidx.navigation.NavGraph.Companion.findStartDestination // Pour trouver la destination initiale du graphe
import androidx.navigation.compose.NavHost // Conteneur pour les écrans navigables
import androidx.navigation.compose.composable // Définit une route spécifique dans le NavHost
import androidx.navigation.compose.currentBackStackEntryAsState // Pour suivre l'écran actuellement affiché
import androidx.navigation.compose.rememberNavController // Pour créer et mémoriser le contrôleur de navigation
import com.example.foodapp.presentation.RecipeViewModel // Le ViewModel qui contient les données
import com.example.foodapp.presentation.UiState // Les états possibles de l'UI (Loading, Success, Error)
import com.example.foodapp.presentation.screens.detail.RecipeDetailScreen // Écran de détail d'une recette
import com.example.foodapp.presentation.screens.home.RecipeListScreen // Écran de la liste principale
import com.example.foodapp.presentation.screens.liked.LikedRecipesScreen // Écran des recettes aimées
import com.example.foodapp.presentation.screens.recent.RecentRecipesScreen // Écran des recettes consultées
import com.example.foodapp.presentation.screens.splash.LoadingScreen // Écran de chargement initial
import com.example.foodapp.presentation.theme.DeepBlack // Couleur noire personnalisée
import com.example.foodapp.presentation.theme.LightLavender // Couleur lavande personnalisée

// Classe scellée définissant les écrans de la barre de navigation
sealed class Screen(val route: String, val icon: @Composable () -> Unit, val label: String) {
    object Home : Screen("list", { Icon(Icons.Default.Home, contentDescription = null) }, "Home")
    object Liked : Screen("liked", { Icon(Icons.Default.Favorite, contentDescription = null) }, "Liked")
    object Recent : Screen("recent", { Icon(Icons.Default.Search, contentDescription = null) }, "Recent")
}

@Composable
fun AppNavigation(viewModel: RecipeViewModel) {
    val navController = rememberNavController() // Initialisation du contrôleur de navigation
    val uiState by viewModel.uiState.collectAsState() // Observation de l'état global (chargement, etc.)
    val navBackStackEntry by navController.currentBackStackEntryAsState() // Récupération de l'entrée actuelle dans la pile
    val currentDestination = navBackStackEntry?.destination // Destination (écran) actuellement affichée

    // Liste des écrans à afficher dans la barre du bas
    val items = listOf(
        Screen.Home,
        Screen.Liked,
        Screen.Recent
    )

    Scaffold(
        bottomBar = { // Définition de la barre de navigation inférieure
            // On affiche la barre uniquement sur les écrans principaux et si le chargement initial est fini
            val isMainScreen = currentDestination?.route in listOf("list", "liked", "recent")
            val isDataLoaded = uiState is UiState.Success

            if (isMainScreen && isDataLoaded) {
                NavigationBar(
                    containerColor = LightLavender, // Couleur de fond de la barre
                    contentColor = DeepBlack // Couleur par défaut du contenu
                ) {
                    items.forEach { screen -> // Pour chaque écran défini dans la liste 'items'
                        NavigationBarItem(
                            icon = screen.icon, // Affichage de l'icône de l'écran
                            label = { Text(screen.label) }, // Affichage du nom sous l'icône
                            // Vérifie si cet item est l'écran actuellement sélectionné
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = { // Action lors du clic sur un bouton de la barre
                                navController.navigate(screen.route) {
                                    // Revient à la destination de départ pour éviter d'accumuler les écrans
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true // Sauvegarde l'état de l'écran qu'on quitte
                                    }
                                    launchSingleTop = true // Évite d'ouvrir plusieurs fois le même écran
                                    restoreState = true // Restaure l'état si l'écran a déjà été ouvert
                                }
                            },
                            colors = NavigationBarItemDefaults.colors( // Personnalisation des couleurs des boutons
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
    ) { innerPadding -> // 'innerPadding' assure que le contenu ne passe pas sous la barre de navigation
        NavHost(
            navController = navController,
            startDestination = "loading", // L'application commence par l'écran de chargement
            modifier = Modifier.padding(innerPadding) // Applique les marges du Scaffold
        ) {
            // Définition de la route pour l'écran de Splash/Chargement
            composable("loading") {
                LoadingScreen(onLoadingFinished = {
                    // Une fois le chargement fini, on va vers la liste principale
                    navController.navigate("list") {
                        popUpTo("loading") { inclusive = true } // On supprime l'écran de chargement de l'historique
                    }
                })
            }

            // Route pour l'écran principal (Liste des recettes)
            composable("list") {
                RecipeListScreen(
                    viewModel = viewModel,
                    uiState = uiState,
                    onRecipeClick = { recipeId ->
                        // Navigation vers le détail lors du clic sur une carte
                        navController.navigate("detail/$recipeId")
                    }
                )
            }

            // Route pour l'écran des recettes aimées
            composable("liked") {
                LikedRecipesScreen(
                    viewModel = viewModel,
                    onRecipeClick = { recipeId ->
                        navController.navigate("detail/$recipeId")
                    }
                )
            }

            // Route pour l'écran des recettes consultées récemment
            composable("recent") {
                RecentRecipesScreen(
                    viewModel = viewModel,
                    onRecipeClick = { recipeId ->
                        navController.navigate("detail/$recipeId")
                    }
                )
            }

            // Route pour l'écran de détail (prend un paramètre 'recipeId')
            composable("detail/{recipeId}") { backStackEntry ->
                val recipeId = backStackEntry.arguments?.getString("recipeId") ?: ""
                RecipeDetailScreen(
                    recipeId = recipeId,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() } // Retourne à l'écran précédent
                )
            }
        }
    }
}
