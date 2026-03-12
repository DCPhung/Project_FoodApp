package com.example.foodapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.foodapp.data.model.Recipe
import com.example.foodapp.ui.RecipeViewModel
import com.example.foodapp.ui.theme.LightLavender
import com.example.foodapp.ui.theme.DeepBlack

/**
 * Écran affichant les détails complets d'une recette.
 * Fond forcé en LightLavender avec texte sombre.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    recipeId: String,
    viewModel: RecipeViewModel,
    onBack: () -> Unit
) {
    var recipe by remember { mutableStateOf<Recipe?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(recipeId) {
        recipe = viewModel.getRecipeById(recipeId)
        isLoading = false
    }

    Scaffold(
        containerColor = LightLavender, // Fond LightLavender
        topBar = {
            TopAppBar(
                title = { Text(recipe?.strMeal ?: "Détail", color = DeepBlack) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour", tint = DeepBlack)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LightLavender
                )
            )
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator(color = DeepBlack)
            }
        } else if (recipe == null) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("Recette non trouvée", color = DeepBlack)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                AsyncImage(
                    model = recipe?.strMealThumb,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    contentScale = ContentScale.Crop
                )
                
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = recipe?.strMeal ?: "",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = DeepBlack
                    )
                    
                    recipe?.strCategory?.let {
                        Text(
                            text = "Catégorie : $it",
                            style = MaterialTheme.typography.bodyLarge,
                            color = DeepBlack.copy(alpha = 0.7f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Ingrédients",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = DeepBlack
                    )
                    
                    recipe?.ingredients?.forEach { ingredient ->
                        Text(
                            text = "• ${ingredient.name} : ${ingredient.measure}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = DeepBlack,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Instructions",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = DeepBlack
                    )
                    
                    Text(
                        text = recipe?.strInstructions ?: "Aucune instruction disponible.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = DeepBlack,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}
