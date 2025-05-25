package com.example.proyectotfg_melodix.Model.MelodixApi

import com.example.proyectotfg_melodix.Model.Request.Song

data class Playlist(
    val id: Int = 0,
    val name: String,
    val description: String
)

data class PlaylistRequest(
    val name: String,
    val description: String
)

data class PlaylistSongRequest(
    val playlistId: Int,
    val songId: String
)

data class PlaylistSongResponse(
    val id: Int,
    val playlist: Playlist,
    val song: SongDto
)

data class SongInPlaylist(
    val playlistSongId: Int,
    val song: Song
)

data class PlaylistWithSongs(
    val id: Int,
    val name: String,
    val description: String,
    val songs: List<SongInPlaylist>
)


