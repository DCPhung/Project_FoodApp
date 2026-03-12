package com.example.foodapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.foodapp.R
import kotlinx.coroutines.delay

/**
 * Écran de chargement (Splash Screen).
 * Affiche le logo de l'application sur un fond noir pur pendant l'initialisation.
 */
@Composable
fun LoadingScreen(onLoadingFinished: () -> Unit) {
    // Effet lancé au premier affichage de l'écran
    LaunchedEffect(Unit) {
        delay(2000) // Attend 2 secondes pour simuler le chargement
        onLoadingFinished() // Appelle le callback pour passer à l'écran suivant
    }

    // Conteneur principal occupant tout l'écran
    Box(
        modifier = Modifier
            .fillMaxSize() // Prend toute la largeur et hauteur disponible
            .background(Color.Black), // Fond forcé en noir pur pour le contraste
        contentAlignment = Alignment.Center // Centre tout le contenu verticalement et horizontalement
    ) {
        // Organisation verticale des éléments
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Affiche le logo de l'application
            Image(
                painter = painterResource(id = R.drawable.foodapp), // Charge l'image depuis res/drawable/foodapp.jpg
                contentDescription = "Logo de l'application", // Description pour l'accessibilité
                modifier = Modifier.size(200.dp) // Définit une taille de 200dp pour le logo
            )
            // Espaceur de 16dp entre le logo et le spinner
            Spacer(modifier = Modifier.height(16.dp))
            // Indicateur de progression circulaire
            CircularProgressIndicator(
                color = Color(0xFFD8B4E3) // Utilise le mauve clair (LightLavender) pour le spinner
            )
        }
    }
}
