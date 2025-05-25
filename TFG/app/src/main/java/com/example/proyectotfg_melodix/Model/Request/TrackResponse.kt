package com.example.proyectotfg_melodix.Model.Request

data class TrackResponse(
    val tracks: List<Track>
)

data class Track(
    val id: String,
    val name: String,
    val preview_url: String?,
    val album: Album,
    val artists: List<Artist>
)

data class Album(
    val images: List<Image>
)

data class Image(
    val url: String
)

data class Artist(
    val id: String,
    val name: String
)

