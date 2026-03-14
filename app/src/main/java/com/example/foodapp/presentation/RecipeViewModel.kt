package com.example.foodapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.foodapp.domain.model.Category
import com.example.foodapp.domain.model.Recipe
import com.example.foodapp.domain.repository.RecipeRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel pour la gestion de l'UI des recettes.
 */
class RecipeViewModel(private val repository: RecipeRepository) : ViewModel() {

    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> = _recipes

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Loading)
    val uiState: StateFlow<UiState<Unit>> = _uiState

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory

    // Gestion des favoris (Liked) - Réinitialisé à chaque lancement
    private val _likedRecipeIds = MutableStateFlow<Set<String>>(emptySet())
    val likedRecipeIds: StateFlow<Set<String>> = _likedRecipeIds

    // Gestion des recettes consultées (Recent) - Réinitialisé à chaque lancement
    private val _recentRecipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recentRecipes: StateFlow<List<Recipe>> = _recentRecipes

    private val _pageSize = 30
    private val _currentPage = MutableStateFlow(1)
    
    private var collectionJob: Job? = null

    val paginatedRecipes = combine(_recipes, _currentPage) { recipes, page ->
        recipes.take(page * _pageSize)
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Recettes aimées filtrées parmi TOUTES les recettes de la DB
    val likedRecipes = repository.getAllRecipes().combine(_likedRecipeIds) { allRecipes, likedIds ->
        allRecipes.filter { it.id in likedIds }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                _categories.value = repository.getCategories()
                repository.refreshRecipes("a")
                observeRecipes(repository.getAllRecipes())
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Erreur inconnue")
            }
        }
    }

    private fun observeRecipes(flow: Flow<List<Recipe>>) {
        collectionJob?.cancel()
        collectionJob = viewModelScope.launch {
            flow.collect {
                _recipes.value = it
                _uiState.value = UiState.Success(Unit)
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        _selectedCategory.value = null
        _currentPage.value = 1
        if (query.isBlank()) {
            observeRecipes(repository.getAllRecipes())
        } else {
            observeRecipes(repository.searchLocal(query))
        }
    }

    fun onCategorySelected(category: String?) {
        _selectedCategory.value = category
        _searchQuery.value = ""
        _currentPage.value = 1
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                if (category == null) {
                    observeRecipes(repository.getAllRecipes())
                } else {
                    repository.filterByCategory(category)
                    observeRecipes(repository.getRecipesByCategoryLocal(category))
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Erreur de filtrage")
            }
        }
    }

    fun toggleLike(recipeId: String) {
        val currentLiked = _likedRecipeIds.value.toMutableSet()
        if (currentLiked.contains(recipeId)) {
            currentLiked.remove(recipeId)
        } else {
            currentLiked.add(recipeId)
        }
        _likedRecipeIds.value = currentLiked
    }

    fun addToRecent(recipe: Recipe) {
        val currentRecent = _recentRecipes.value.toMutableList()
        currentRecent.removeAll { it.id == recipe.id }
        currentRecent.add(0, recipe)
        _recentRecipes.value = currentRecent.take(20) // On garde les 20 dernières
    }

    fun loadMore() {
        if (_currentPage.value * _pageSize < _recipes.value.size) {
            _currentPage.value += 1
        }
    }

    fun retry() {
        loadInitialData()
    }

    suspend fun getRecipeById(id: String): Recipe? {
        val recipe = repository.getRecipeById(id)
        recipe?.let { addToRecent(it) }
        return recipe
    }
}

class RecipeViewModelFactory(private val repository: RecipeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecipeViewModel(repository) as T
        }
        throw IllegalArgumentException("Classe ViewModel inconnue")
    }
}
