package com.example.proyectotfg_melodix.Model.Request

data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val artistId: String,
    val artistImageUrl: String,
    val imageUrl: String,
    val previewUrl: String?,  // URL de previsualización
    val requestUrl: String,
    val isDownloaded: Boolean = false, // Indica si está descargada localmente
    val localPath: String? = null, // Ruta donde se almacena si está descargada
    val isFavorite: Boolean = false,
    val idApi: String? = null, // <- nuevo campo
    val idFavorite: Long? = null
)

