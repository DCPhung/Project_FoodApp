package com.example.foodapp.presentation

/**
 * Représente les différents états possibles de l'interface utilisateur.
 */
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
