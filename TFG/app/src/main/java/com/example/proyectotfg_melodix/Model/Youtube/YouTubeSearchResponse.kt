package com.example.proyectotfg_melodix.Model.Youtube

data class YouTubeSearchResponse(
    val items: List<YouTubeSearchItem>
)

data class YouTubeSearchItem(
    val id: VideoId
)

data class VideoId(
    val videoId: String
)

