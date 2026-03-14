package com.example.foodapp.presentation.screens.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.foodapp.domain.model.Recipe
import com.example.foodapp.presentation.RecipeViewModel
import com.example.foodapp.presentation.theme.LightLavender
import com.example.foodapp.presentation.theme.DeepBlack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    recipeId: String,
    viewModel: RecipeViewModel,
    onBack: () -> Unit
) {
    var recipe by remember { mutableStateOf<Recipe?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val likedRecipeIds by viewModel.likedRecipeIds.collectAsState()
    val isLiked = likedRecipeIds.contains(recipeId)

    LaunchedEffect(recipeId) {
        recipe = viewModel.getRecipeById(recipeId)
        isLoading = false
    }

    Scaffold(
        containerColor = LightLavender,
        topBar = {
            TopAppBar(
                title = { Text(recipe?.name ?: "Détail", color = DeepBlack) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour", tint = DeepBlack)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LightLavender)
            )
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = DeepBlack)
            }
        } else if (recipe == null) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text("Recette non trouvée", color = DeepBlack)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                Box(modifier = Modifier.fillMaxWidth().height(250.dp)) {
                    AsyncImage(
                        model = recipe?.thumbnailUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    
                    FloatingActionButton(
                        onClick = { viewModel.toggleLike(recipeId) },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp),
                        containerColor = LightLavender,
                        contentColor = DeepBlack
                    ) {
                        Icon(
                            imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (isLiked) "Unlike" else "Like",
                            tint = if (isLiked) androidx.compose.ui.graphics.Color.Red else DeepBlack
                        )
                    }
                }
                
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = recipe?.name ?: "",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = DeepBlack
                    )
                    
                    recipe?.category?.let {
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
                        text = recipe?.instructions ?: "Aucune instruction disponible.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = DeepBlack,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}
