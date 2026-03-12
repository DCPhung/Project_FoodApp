package com.example.foodapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

/**
 * Configuration de la palette de couleurs pour le Mode Sombre (Dark Mode).
 * 
 * - primary : Couleur principale (Lavande claire) pour les actions et accents.
 * - secondary : Couleur secondaire (Mauve moyen) pour le relief.
 * - tertiary : Couleur tertiaire (Blanc) pour les détails fins.
 * - background : Fond de l'application en mauve très sombre (DarkPurple).
 * - surface : Couleur des composants (cartes, menus) en SurfaceDark pour voir les contours.
 * - onPrimary/onSecondary : Texte noir sur les boutons de couleur.
 * - onBackground/onSurface : Texte blanc sur les fonds sombres.
 */
private val DarkColorScheme = darkColorScheme(
    primary = LightLavender,
    secondary = MediumPurple,
    tertiary = White,
    background = DarkPurple,
    surface = SurfaceDark,
    onPrimary = DeepBlack,
    onSecondary = DeepBlack,
    onBackground = White,
    onSurface = White
)

/**
 * Configuration de la palette de couleurs pour le Mode Clair (Light Mode).
 * 
 * - primary : Mauve moyen pour les éléments principaux.
 * - secondary : Lavande claire pour les éléments secondaires.
 * - background : Fond DeepPurple pour la liste des recettes (contraste fort).
 * - surface : Lavande claire pour la barre de recherche et les cartes.
 * - onBackground : Texte blanc pour être lisible sur le fond DeepPurple.
 * - onSurface : Texte noir pour les éléments sur fond Lavande.
 */
private val LightColorScheme = lightColorScheme(
    primary = MediumPurple,
    secondary = LightLavender,
    tertiary = DeepBlack,
    background = DeepPurple,
    surface = LightLavender,
    onPrimary = DeepPurple,
    onSecondary = DeepBlack,
    onBackground = White,
    onSurface = DeepBlack
)

/**
 * Composant de thème principal de l'application FoodApp.
 * 
 * @param darkTheme Détermine si le thème sombre doit être appliqué (détecté automatiquement par défaut).
 * @param dynamicColor Désactivé (false) pour garantir l'utilisation de notre charte graphique personnalisée.
 * @param content Le contenu de l'application qui recevra ces styles.
 */
@Composable
fun FoodAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // Sélectionne la palette en fonction du mode (Sombre ou Clair)
    val colorScheme = when {
        // Optionnel : Couleurs dynamiques Android 12+ (ignoré car dynamicColor = false)
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Applique les couleurs, la typographie et le contenu au thème Material3
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
