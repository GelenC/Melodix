package com.example.proyectotfg_melodix.ViewModel.Search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectotfg_melodix.Model.Request.ArtistItem
import com.example.proyectotfg_melodix.Model.Request.Track
import com.example.proyectotfg_melodix.Model.Youtube.YoutubeSearch
import com.example.proyectotfg_melodix.Retrofit.Request.SpotifyRepository
import com.example.proyectotfg_melodix.Retrofit.Youtube.YouTubeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val youTubeRepository: YouTubeRepository,
    private val spotifyRepository: SpotifyRepository
) : ViewModel() {

    private val _searchResults = MutableStateFlow<List<Track>>(emptyList())
    val searchResults: StateFlow<List<Track>> = _searchResults

    private val _artistResults = MutableStateFlow<List<ArtistItem>>(emptyList())
    val artistResults: StateFlow<List<ArtistItem>> = _artistResults

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun search(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val rawYouTubeResults = youTubeRepository.searchVideos(query)

            val token = spotifyRepository.getValidToken()
            if (token == null) {
                _isLoading.value = false
                return@launch
            }

            val artists = spotifyRepository.searchArtists(query, token)
            _artistResults.value = artists

            val matchingTracks = mutableListOf<Track>()

            for (youtubeResult in rawYouTubeResults) {
                val tracks = spotifyRepository.searchTrack(youtubeResult.title, token)
                if (tracks.isNotEmpty()) {
                    matchingTracks.add(tracks.first()) //Usamos solo el primer resultado de Spotify
                }
            }

            _searchResults.value = matchingTracks.distinctBy { it.id }
            _isLoading.value = false
        }
    }

    fun clearResults() {
        _searchResults.value = emptyList()
        _artistResults.value = emptyList()
    }

}
