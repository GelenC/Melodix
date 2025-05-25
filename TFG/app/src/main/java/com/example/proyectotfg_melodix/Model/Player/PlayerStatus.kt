package com.example.proyectotfg_melodix.Model.Player

import com.example.proyectotfg_melodix.Model.Request.Song

enum class PlayerStatus {
    IDLE, LOADING, PLAYING, PAUSED, ERROR
}

data class PlayerState(
    val currentSong: Song? = null,
    val currentVideoId: String? = null,
    val isPlaying: Boolean = false,
    val playlist: List<Song>? = null,
    val currentIndex: Int = 0
)

enum class PlaybackMode {
    REPEAT_ONE,
    STOP_AT_END,
    PLAY_NEXT
}


