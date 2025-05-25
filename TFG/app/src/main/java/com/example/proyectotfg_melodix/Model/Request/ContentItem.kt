package com.example.proyectotfg_melodix.Model.Request
import com.example.proyectotfg_melodix.Model.Request.ArtistItem as RawArtistItem
sealed class ContentItem {
    data class SongItem(val song: Song) : ContentItem()
    data class ArtistItem(val artist: RawArtistItem) : ContentItem()
}
