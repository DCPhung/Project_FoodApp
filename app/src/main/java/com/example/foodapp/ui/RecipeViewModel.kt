package com.example.foodapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.model.Category
import com.example.foodapp.data.model.Recipe
import com.example.foodapp.data.repository.RecipeRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

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

    // Simulated pagination
    private val _pageSize = 30
    private val _currentPage = MutableStateFlow(1)
    
    val paginatedRecipes = combine(_recipes, _currentPage) { recipes, page ->
        recipes.take(page * _pageSize)
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                _categories.value = repository.getCategories()
                repository.refreshRecipes()
                repository.allRecipes.collect {
                    _recipes.value = it
                    _uiState.value = UiState.Success(Unit)
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        _selectedCategory.value = null
        viewModelScope.launch {
            if (query.isBlank()) {
                repository.allRecipes.collect { _recipes.value = it }
            } else {
                repository.searchLocal(query).collect { _recipes.value = it }
            }
        }
    }

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
                    repository.filterByCategory(category)
                    repository.getRecipesByCategoryLocal(category).collect {
                        _recipes.value = it
                        _uiState.value = UiState.Success(Unit)
                    }
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Error filtering by category")
            }
        }
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
        return repository.getRecipeById(id)
    }
}

class RecipeViewModelFactory(private val repository: RecipeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecipeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}