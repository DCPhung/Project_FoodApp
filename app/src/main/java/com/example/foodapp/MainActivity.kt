package com.example.foodapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodapp.data.local.AppDatabase
import com.example.foodapp.data.remote.RetrofitClient
import com.example.foodapp.data.repository.RecipeRepositoryImpl
import com.example.foodapp.presentation.RecipeViewModel
import com.example.foodapp.presentation.RecipeViewModelFactory
import com.example.foodapp.presentation.navigation.AppNavigation
import com.example.foodapp.presentation.theme.FoodAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val database = AppDatabase.getDatabase(this)
        val repository = RecipeRepositoryImpl(RetrofitClient.mealApi, database.recipeDao())
        val viewModelFactory = RecipeViewModelFactory(repository)

        enableEdgeToEdge()
        
        setContent {
            FoodAppTheme {
                val viewModel: RecipeViewModel = viewModel(factory = viewModelFactory)
                AppNavigation(viewModel)
            }
        }
    }
}
