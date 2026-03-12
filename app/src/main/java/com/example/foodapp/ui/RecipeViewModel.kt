package com.example.foodapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.model.Category
import com.example.foodapp.data.model.Recipe
import com.example.foodapp.data.repository.RecipeRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Représente les différents états possibles de l'interface utilisateur.
 * C'est une classe scellée (sealed) qui permet de gérer proprement le chargement, le succès et l'erreur.
 */
sealed class UiState<out T> {
    object Loading : UiState<Nothing>() // L'application est en train de charger des données
    data class Success<T>(val data: T) : UiState<T>() // Les données ont été récupérées avec succès
    data class Error(val message: String) : UiState<Nothing>() // Une erreur est survenue
}

/**
 * Le ViewModel gère la logique de l'interface utilisateur et survit aux changements de configuration (comme la rotation de l'écran).
 * Il communique avec le Repository pour obtenir les données et les expose sous forme de "StateFlow".
 */
class RecipeViewModel(private val repository: RecipeRepository) : ViewModel() {

    // _recipes est privé pour éviter les modifications directes depuis l'extérieur
    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    // recipes est public et immuable, c'est ce que l'interface observe
    val recipes: StateFlow<List<Recipe>> = _recipes

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Loading)
    val uiState: StateFlow<UiState<Unit>> = _uiState

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory

    // Gestion de la pagination (affichage par morceaux pour la performance)
    private val _pageSize = 30
    private val _currentPage = MutableStateFlow(1)
    
    // combine permet de créer un nouveau flux de données basé sur les recettes et la page actuelle
    val paginatedRecipes = combine(_recipes, _currentPage) { recipes, page ->
        recipes.take(page * _pageSize)
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        // Chargement initial des données au démarrage du ViewModel
        loadInitialData()
    }

    /**
     * Charge les catégories et les premières recettes.
     */
    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                // On récupère les catégories depuis le web
                _categories.value = repository.getCategories()
                // On rafraîchit la liste des recettes (par défaut celles commençant par 'a')
                repository.refreshRecipes()
                // On écoute (collect) les changements dans la base de données locale
                repository.allRecipes.collect {
                    _recipes.value = it
                    _uiState.value = UiState.Success(Unit)
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Erreur inconnue")
            }
        }
    }

    /**
     * Appelé quand l'utilisateur tape dans la barre de recherche.
     */
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        _selectedCategory.value = null // On désélectionne la catégorie si on fait une recherche textuelle
        viewModelScope.launch {
            if (query.isBlank()) {
                // Si vide, on réaffiche tout
                repository.allRecipes.collect { _recipes.value = it }
            } else {
                // Sinon, on cherche en local
                repository.searchLocal(query).collect { _recipes.value = it }
            }
        }
    }

    /**
     * Appelé quand l'utilisateur clique sur un bouton de catégorie.
     */
    fun onCategorySelected(category: String?) {
        _selectedCategory.value = category
        _searchQuery.value = ""
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                if (category == null) {
                    repository.allRecipes.collect { 
                        _recipes.value = it 
                        _uiState.value = UiState.Success(Unit)
                    }
                } else {
                    // On demande au repository de télécharger les recettes de cette catégorie
                    repository.filterByCategory(category)
                    // On affiche les résultats filtrés
                    repository.getRecipesByCategoryLocal(category).collect {
                        _recipes.value = it
                        _uiState.value = UiState.Success(Unit)
                    }
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Erreur lors du filtrage par catégorie")
            }
        }
    }

    /**
     * Augmente le nombre d'éléments affichés (scroll infini).
     */
    fun loadMore() {
        if (_currentPage.value * _pageSize < _recipes.value.size) {
            _currentPage.value += 1
        }
    }

    /**
     * Relance le chargement si une erreur est survenue.
     */
    fun retry() {
        loadInitialData()
    }

    /**
     * Récupère une recette spécifique (utilisé par l'écran de détails).
     */
    suspend fun getRecipeById(id: String): Recipe? {
        return repository.getRecipeById(id)
    }
}

/**
 * Classe nécessaire pour créer le ViewModel avec ses dépendances (ici le repository).
 */
class RecipeViewModelFactory(private val repository: RecipeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecipeViewModel(repository) as T
        }
        throw IllegalArgumentException("Classe ViewModel inconnue")
    }
}
