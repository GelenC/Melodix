package com.example.proyectotfg_melodix.Model.Request

data class ArtistSearchResponse(
    val artists: ArtistResult
)

data class ArtistResult(
    val items: List<ArtistItem>
)

data class ArtistItem(
    val id: String,
    val name: String,
    val images: List<Image>,
    val idApi: String? = null
)

// Nuevo modelo solo para géneros
data class ArtistGenresResponse(
    val id: String,
    val genres: List<String>
)
