package com.example.foodapp.presentation.screens.recent // Définition du paquetage pour l'écran des recettes consultées

import androidx.compose.foundation.layout.* // Import pour la mise en page (Box, Column, padding, etc.)
import androidx.compose.foundation.lazy.LazyColumn // Liste verticale optimisée pour de nombreux éléments
import androidx.compose.foundation.lazy.items // Extension pour itérer sur les données dans une LazyColumn
import androidx.compose.material3.* // Composants d'interface Material Design 3
import androidx.compose.runtime.Composable // Annotation pour les fonctions d'UI Compose
import androidx.compose.runtime.collectAsState // Convertit un Flow en état observable par Compose
import androidx.compose.runtime.getValue // Permet l'utilisation de la délégation 'by'
import androidx.compose.ui.Alignment // Outils d'alignement pour les conteneurs
import androidx.compose.ui.Modifier // Outils pour modifier l'apparence des composants
import androidx.compose.ui.graphics.Color // Gestion des couleurs
import androidx.compose.ui.unit.dp // Unité de mesure pour les dimensions (pixels indépendants de la densité)
import com.example.foodapp.presentation.RecipeViewModel // Le ViewModel gérant les données de l'application
import com.example.foodapp.presentation.screens.home.RecipeCard // Composant réutilisable pour afficher une carte de recette
import com.example.foodapp.presentation.theme.DeepBlack // Couleur noire personnalisée du thème
import com.example.foodapp.presentation.theme.LightLavender // Couleur lavande personnalisée du thème

@OptIn(ExperimentalMaterial3Api::class) // Utilisation d'APIs Material 3 en phase expérimentale (ex: TopAppBar)
@Composable
fun RecentRecipesScreen(
    viewModel: RecipeViewModel, // Injection du ViewModel pour accéder à la liste des recettes récentes
    onRecipeClick: (String) -> Unit // Action déclenchée lorsqu'on clique sur une recette (navigation vers le détail)
) {
    // Observation de la liste des recettes consultées récemment via un StateFlow du ViewModel
    val recentRecipes by viewModel.recentRecipes.collectAsState()

    Scaffold(
        topBar = {
            // Barre de titre fixe en haut de l'écran
            CenterAlignedTopAppBar(
                title = { Text("Dernières recettes consultées", color = DeepBlack) }, // Titre centré
                // Définition de la couleur de fond lavande pour la barre
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = LightLavender)
            )
        },
        containerColor = MaterialTheme.colorScheme.background // Définit le fond de l'écran avec la couleur du thème
    ) { innerPadding -> // innerPadding contient les marges nécessaires pour ne pas chevaucher la TopBar
        if (recentRecipes.isEmpty()) {
            // Si l'historique est vide, on affiche un message d'information au centre de l'écran
            Box(
                modifier = Modifier
                    .fillMaxSize() // Prend tout l'espace disponible
                    .padding(innerPadding), // Respecte les marges définies par le Scaffold
                contentAlignment = Alignment.Center // Centre le contenu textuel
            ) {
                Text("Aucune recette consultée récemment", color = Color.White)
            }
        } else {
            // Si des recettes ont été consultées, on les affiche dans une liste défilante
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize() // Remplit l'écran
                    .padding(innerPadding), // Respecte les marges du Scaffold
                contentPadding = PaddingValues(16.dp), // Ajoute des marges intérieures autour de la liste
                verticalArrangement = Arrangement.spacedBy(16.dp) // Ajoute un espace de 16dp entre chaque carte
            ) {
                // Itération sur la liste des recettes récentes
                items(recentRecipes, key = { it.id }) { recipe ->
                    // Affichage de chaque recette sous forme de carte cliquable
                    RecipeCard(
                        recipe = recipe,
                        onClick = { onRecipeClick(recipe.id) } // Navigue vers l'écran de détail au clic
                    )
                }
            }
        }
    }
}
