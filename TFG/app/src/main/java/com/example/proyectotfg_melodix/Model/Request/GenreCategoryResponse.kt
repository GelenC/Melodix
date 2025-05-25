package com.example.proyectotfg_melodix.Model.Request

data class GenreCategoryResponse(
    val categories: Categories
)

data class Categories(
    val items: List<GenreCategory>
)

data class GenreCategory(
    val id: String,
    val name: String,
    val icons: List<Image> // Reutilizar clase `Image`
)

