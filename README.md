# FoodApp - Application de Recettes de Cuisine

Bienvenue dans le projet **FoodApp** ! Cette application Android permet de rechercher des recettes, de les consulter par catégorie et d'afficher les détails (ingrédients et instructions) pour chaque plat.

Elle a été conçue pour être simple, performante et facile à comprendre pour un développeur débutant.

## 🚀 Architecture du Projet

L'application suit l'architecture **MVVM** (Model-View-ViewModel), qui est la recommandation officielle de Google pour le développement Android moderne.

### 📁 Structure des dossiers

Le code source se trouve dans `app/src/main/java/com/example/foodapp/` :

*   **`data/`** : Gère tout ce qui concerne les données.
    *   **`local/`** : Contient la base de données locale (**Room**). Cela permet à l'application de fonctionner même sans connexion internet une fois que les données ont été chargées une première fois.
    *   **`remote/`** : Gère les appels vers l'API externe (**Retrofit**). Nous utilisons l'API gratuite [TheMealDB](https://www.themealdb.com/).
    *   **`model/`** : Contient les classes de données (Data Classes) qui représentent une Recette, une Catégorie, etc.
    *   **`repository/`** : C'est le "cerveau" des données. Il décide s'il doit aller chercher l'information sur Internet ou dans la base de données locale.

*   **`ui/`** : Gère l'interface utilisateur (User Interface).
    *   **`screens/`** : Contient les différents écrans de l'application (Chargement, Liste, Détails) créés avec **Jetpack Compose**.
    *   **`theme/`** : Définit les couleurs, la typographie et le style visuel de l'application.
    *   **`RecipeViewModel.kt`** : Prépare les données pour les écrans et gère la logique d'interaction (clics, recherche).

*   **`MainActivity.kt`** : Le point de départ de l'application qui lance la navigation.

## 🛠️ Technologies utilisées

*   **Kotlin** : Le langage de programmation moderne pour Android.
*   **Jetpack Compose** : Pour créer l'interface utilisateur de manière moderne et déclarative.
*   **Room** : Pour stocker les données sur le téléphone (Base de données SQLite).
*   **Retrofit** : Pour récupérer les données depuis Internet.
*   **Coil** : Pour charger et afficher les images des recettes.
*   **Navigation Compose** : Pour passer d'un écran à un autre.


