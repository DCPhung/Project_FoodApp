# FoodApp - Application de Recettes de Cuisine

FoodApp est une application Android moderne permettant de découvrir des recettes de cuisine du monde entier en utilisant l'API [TheMealDB](https://www.themealdb.com/api.php). L'application est bâtie avec les dernières technologies recommandées pour le développement Android (Jetpack Compose, MVVM, Clean Architecture).

## 🚀 Fonctionnalités

- **Découverte de recettes** : Parcourez une vaste liste de recettes avec pagination.
- **Recherche et Filtrage** : Recherchez des recettes par nom ou filtrez-les par catégories (Boeuf, Poulet, Desserts, etc.).
- **Détails Complets** : Consultez les ingrédients, les quantités et les instructions détaillées pour chaque plat.
- **Favoris (Liked)** : Aimez vos recettes préférées pour les retrouver facilement dans un onglet dédié (sauvegarde en mémoire vive).
- **Historique (Recent)** : Accédez rapidement aux dernières recettes que vous avez consultées.
- **Navigation Fluide** : Une barre de navigation intuitive en bas de l'écran pour basculer entre l'accueil, les favoris et l'historique.
- **Mode Hors-ligne (Cache)** : Les recettes consultées sont mises en cache localement via Room pour une consultation rapide.

## 🛠 Stack Technique

- **Langage** : Kotlin
- **UI** : Jetpack Compose (Material Design 3)
- **Architecture** : MVVM (Model-View-ViewModel) avec les principes de Clean Architecture.
- **Injection de dépendances** : Gestion manuelle (ou via ViewModelFactory).
- **Réseau** : Retrofit & Gson pour la consommation de l'API REST.
- **Base de données** : Room Persistence Library pour le cache local.
- **Navigation** : Jetpack Navigation Compose.
- **Images** : Coil pour le chargement asynchrone des images.
- **Asynchronisme** : Kotlin Coroutines & Flow.

## 📁 Structure du Projet

```text
com.example.foodapp
├── data                # Couche Data (Repositories, API, DTOs, Local DB)
│   ├── local           # Room DB, Entities, DAOs
│   ├── remote          # Retrofit Service, DTOs
│   └── repository      # Implémentation des repositories
├── domain              # Couche Domain (Business Logic)
│   ├── model           # Modèles de données métier
│   └── repository      # Interfaces des repositories
└── presentation        # Couche UI (Jetpack Compose)
    ├── navigation      # Configuration de la navigation et BottomBar
    ├── screens         # Écrans de l'application (Home, Detail, Liked, Recent, Splash)
    ├── theme           # Couleurs, Typographie et Thème
    └── RecipeViewModel # Gestion de l'état de l'UI
```

## ⚙️ Installation

1. Clonez le dépôt 
2. Ouvrez le projet dans **Android Studio** (Version Ladybug ou ultérieure recommandée).
3. Laissez Gradle synchroniser les dépendances.
4. Lancez l'application sur un émulateur ou un appareil physique (Min SDK 24).

## 📝 Notes de développement

- L'application utilise une architecture réactive grâce à `StateFlow` et `Flow`.
- Les listes de favoris et les recettes récentes sont réinitialisées à chaque lancement de l'application (stockage en mémoire).
- La barre de navigation s'affiche dynamiquement une fois que les données initiales sont chargées pour garantir une expérience utilisateur fluide.

---
