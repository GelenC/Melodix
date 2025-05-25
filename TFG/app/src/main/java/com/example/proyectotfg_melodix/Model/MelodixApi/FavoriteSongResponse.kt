package com.example.proyectotfg_melodix.Model.MelodixApi

data class FavoriteSongResponse(
    val id: Long,
    val song: SongDto
)

data class SongDto(
    val id: String,
    val spotifySongId: String,
    val title: String,
    val artistName: String,
    val artistId: String,
    val albumImageUrl: String,
    val previewUrl: String,
    val requestUrl: String
)

data class AddFavoriteSongRequest(
    val spotifySongId: String,
    val title: String,
    val artistName: String,
    val imageUrl: String,
    val previewUrl: String
)

data class SongRequest(
    val spotifySongId: String,
    val title: String,
    val artistName: String,
    val artistId: String,
    val albumImageUrl: String,
    val previewUrl: String,
    val requestUrl: String
)

data class FavoriteSongRequest(
    val songId: String // Este es el ID generado por la API (el que devuelve la tabla songs)
)

data class SongApiResponse(
    val id: String,  // ID interno generado por la API (no el de Spotify)
    val spotifySongId: String,
    val title: String,
    val artistName: String,
    val artistId: String,
    val albumImageUrl: String,
    val previewUrl: String,
    val requestUrl: String
)


