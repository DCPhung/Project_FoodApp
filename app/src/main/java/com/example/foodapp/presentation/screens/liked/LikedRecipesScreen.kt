package com.example.foodapp.presentation.screens.liked // Définition du paquetage pour l'écran des favoris

import androidx.compose.foundation.layout.* // Imports pour la mise en page (Box, Column, padding, etc.)
import androidx.compose.foundation.lazy.LazyColumn // Liste verticale optimisée pour de nombreux éléments
import androidx.compose.foundation.lazy.items // Extension pour itérer sur les données dans une LazyColumn
import androidx.compose.material3.* // Composants d'interface Material Design 3
import androidx.compose.runtime.Composable // Annotation pour les fonctions d'UI Compose
import androidx.compose.runtime.collectAsState // Convertit un Flow en état observable par Compose
import androidx.compose.runtime.getValue // Permet l'utilisation de la délégation 'by'
import androidx.compose.ui.Alignment // Outils d'alignement pour les conteneurs
import androidx.compose.ui.Modifier // Outils pour modifier l'apparence des composants
import androidx.compose.ui.graphics.Color // Gestion des couleurs
import androidx.compose.ui.unit.dp // Unité de mesure pour les dimensions
import com.example.foodapp.presentation.RecipeViewModel // Le ViewModel gérant les données
import com.example.foodapp.presentation.screens.home.RecipeCard // Composant de carte de recette
import com.example.foodapp.presentation.theme.DeepBlack // Couleur noire personnalisée
import com.example.foodapp.presentation.theme.LightLavender // Couleur lavande personnalisée

@OptIn(ExperimentalMaterial3Api::class) // Utilisation d'APIs Material 3 en phase expérimentale
@Composable
fun LikedRecipesScreen(
    viewModel: RecipeViewModel, // Accès aux données via le ViewModel
    onRecipeClick: (String) -> Unit // Action à effectuer lors du clic sur une recette
) {
    // On récupère la liste des recettes aimées depuis le ViewModel
    val likedRecipes by viewModel.likedRecipes.collectAsState()

    Scaffold(
        topBar = {
            // Barre de titre en haut de l'écran
            CenterAlignedTopAppBar(
                title = { Text("Recettes likées", color = DeepBlack) }, // Titre centré
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = LightLavender) // Couleur de la barre
            )
        },
        containerColor = MaterialTheme.colorScheme.background // Couleur de fond de l'application
    ) { innerPadding -> // Marges automatiques pour ne pas chevaucher la barre du haut
        if (likedRecipes.isEmpty()) {
            // Si aucune recette n'est aimée, on affiche un message au centre
            Box(
                modifier = Modifier
                    .fillMaxSize() // Prend tout l'écran
                    .padding(innerPadding), // Respecte les marges du Scaffold
                contentAlignment = Alignment.Center // Centre le texte
            ) {
                Text("Aucune recette aimée pour le moment", color = Color.White)
            }
        } else {
            // Sinon, on affiche la liste des recettes sous forme de cartes
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp), // Marges autour de la liste
                verticalArrangement = Arrangement.spacedBy(16.dp) // Espace entre les cartes
            ) {
                // Pour chaque recette aimée, on crée une carte cliquable
                items(likedRecipes, key = { it.id }) { recipe ->
                    RecipeCard(
                        recipe = recipe,
                        onClick = { onRecipeClick(recipe.id) } // Navigue vers le détail au clic
                    )
                }
            }
        }
    }
}
