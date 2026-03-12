package com.example.foodapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.foodapp.data.model.Category
import com.example.foodapp.data.model.Recipe
import com.example.foodapp.ui.RecipeViewModel
import com.example.foodapp.ui.UiState
import com.example.foodapp.ui.theme.LightLavender
import com.example.foodapp.ui.theme.DeepBlack

/**
 * Écran principal affichant la liste des recettes.
 * Gère la recherche, le filtrage par catégorie et la pagination des résultats.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeListScreen(
    viewModel: RecipeViewModel,
    uiState: UiState<Unit>,
    onRecipeClick: (String) -> Unit
) {
    // Collecte des états depuis le ViewModel
    val recipes by viewModel.paginatedRecipes.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    
    // État de la liste pour gérer le défilement et la pagination
    val listState = rememberLazyListState()

    // Détermine s'il faut charger plus de données quand on arrive en bas de la liste
    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItemIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleItemIndex >= recipes.size - 5 && recipes.isNotEmpty()
        }
    }

    // Déclenche le chargement de la page suivante
    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value) {
            viewModel.loadMore()
        }
    }

    Scaffold(
        topBar = {
            // Barre supérieure contenant la recherche et les filtres
            Column(modifier = Modifier.statusBarsPadding()) {
                // Champ de saisie pour la recherche textuelle
                TextField(
                    value = searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Rechercher une recette...", color = DeepBlack.copy(alpha = 0.6f)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Icône", tint = DeepBlack) },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = LightLavender, // Fond lavande quand actif
                        unfocusedContainerColor = LightLavender, // Fond lavande quand inactif
                        focusedTextColor = DeepBlack,
                        unfocusedTextColor = DeepBlack,
                        cursorColor = DeepBlack
                    )
                )
                
                // Liste horizontale des catégories (Chips)
                CategoryList(
                    categories = categories,
                    selectedCategory = selectedCategory,
                    onCategorySelected = { viewModel.onCategorySelected(it) }
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background // Utilise DeepPurple défini dans le thème
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            // Gestion visuelle des différents états de l'UI
            when (uiState) {
                is UiState.Loading -> {
                    if (recipes.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = LightLavender)
                        }
                    }
                }
                is UiState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Erreur : ${uiState.message}", color = Color.White)
                        Button(onClick = { viewModel.retry() }) {
                            Text("Réessayer")
                        }
                    }
                }
                is UiState.Success -> {
                    if (recipes.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Aucune recette trouvée", color = Color.White)
                        }
                    }
                }
            }

            // Grille/Liste verticale des recettes
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(recipes, key = { it.idMeal }) { recipe ->
                    RecipeCard(recipe = recipe, onClick = { onRecipeClick(recipe.idMeal) })
                }
                
                // Spinner de fin de liste pour la pagination
                if (uiState is UiState.Loading && recipes.isNotEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = LightLavender)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Affiche une rangée de filtres pour les catégories.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryList(
    categories: List<Category>,
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            // Option "Toutes" les catégories
            FilterChip(
                selected = selectedCategory == null,
                onClick = { onCategorySelected(null) },
                label = { Text("Toutes") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = LightLavender,
                    selectedLabelColor = DeepBlack,
                    containerColor = LightLavender.copy(alpha = 0.3f), // Lavande transparent si non sélectionné
                    labelColor = Color.White
                )
            )
        }
        items(categories, key = { it.idCategory }) { category ->
            // Filtre par catégorie spécifique
            FilterChip(
                selected = selectedCategory == category.strCategory,
                onClick = { onCategorySelected(category.strCategory) },
                label = { Text(category.strCategory) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = LightLavender,
                    selectedLabelColor = DeepBlack,
                    containerColor = LightLavender.copy(alpha = 0.3f),
                    labelColor = Color.White
                )
            )
        }
    }
}

/**
 * Composant de carte affichant un aperçu d'une recette.
 */
@Composable
fun RecipeCard(recipe: Recipe, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface) // LightLavender via theme
    ) {
        Column {
            // Image de la recette chargée via Coil
            AsyncImage(
                model = recipe.strMealThumb,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(200.dp),
                contentScale = ContentScale.Crop
            )
            // Titre de la recette
            Text(
                text = recipe.strMeal,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.titleLarge,
                color = DeepBlack
            )
        }
    }
}
