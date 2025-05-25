package com.example.proyectotfg_melodix.Model.MelodixApi

data class FavoriteArtistApiResponse(
    val id: String,
    val spotifyArtistId: String,
    val name: String,
    val imageUrl: String
)

data class FavoriteArtistApiRequest(
    val spotifyArtistId: String,
    val name: String,
    val imageUrl: String
)

