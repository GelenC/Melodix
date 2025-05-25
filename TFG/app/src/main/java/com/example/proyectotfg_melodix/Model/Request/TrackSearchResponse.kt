package com.example.proyectotfg_melodix.Model.Request

data class TrackSearchResponse(
    val tracks: TrackItems
)

data class TrackItems(
    val items: List<Track>
)

