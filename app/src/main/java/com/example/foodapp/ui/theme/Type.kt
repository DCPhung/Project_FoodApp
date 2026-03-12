package com.example.foodapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Configuration de la typographie (polices de caractères) pour l'application.
 * On définit ici les styles de texte par défaut (tailles, graisses, espacements).
 */
val Typography = Typography(
    // Style pour les longs textes (corps de texte)
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* 
    Il est possible de personnaliser d'autres styles comme :
    titleLarge (Titres), labelSmall (Petites étiquettes), etc.
    */
)
