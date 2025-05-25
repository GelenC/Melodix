package com.example.proyectotfg_melodix.Model.Youtube

data class YoutubeSearch(
    val videoId: String,
    val title: String,
    val channelTitle: String,
    val thumbnailUrl: String,
    val matchedInSpotify: Boolean = false // para saber si pasó el filtro
)
