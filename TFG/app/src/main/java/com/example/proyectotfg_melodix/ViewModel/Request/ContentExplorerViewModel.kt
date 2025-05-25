package com.example.proyectotfg_melodix.ViewModel.Request

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectotfg_melodix.Model.Request.ArtistItem
import com.example.proyectotfg_melodix.Model.Request.ContentItem
import com.example.proyectotfg_melodix.Model.Request.ContentSection
import com.example.proyectotfg_melodix.Model.Request.Song
import com.example.proyectotfg_melodix.Retrofit.MelodixApi.MelodixRepository
import com.example.proyectotfg_melodix.Retrofit.Request.SpotifyRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ContentExplorerViewModel(private val repository: SpotifyRepository, private val melodixRepository: MelodixRepository) : ViewModel() {

    private val _contentSections = MutableStateFlow<List<ContentSection>>(emptyList())
    val contentSections: StateFlow<List<ContentSection>> = _contentSections

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _artistSongs = MutableStateFlow<List<Song>>(emptyList())
    val artistSongs: StateFlow<List<Song>> = _artistSongs

    private val _selectedArtist = MutableStateFlow<ArtistItem?>(null)
    val selectedArtist: StateFlow<ArtistItem?> = _selectedArtist


    private var currentArtistOffset = 0
    private var currentCount = 0
    private val batchSize = 10
    private val artistBatchSize = 10


    init {
        loadPersonalizedSections()
    }

    fun loadMoreSections() {
        viewModelScope.launch {
            _isLoading.value = true
            val newSections = repository.getMultipleSections(batchSize)
            val combined = _contentSections.value + newSections
            _contentSections.value = combined
            currentCount += batchSize
            _isLoading.value = false
        }
    }

    fun resetArtistSongs() {
        _artistSongs.value = emptyList()
        currentArtistOffset = 0
    }

    suspend fun getValidSpotifyToken(): String? {
        return repository.getValidToken()
    }

    suspend fun getArtistInfo(artistId: String, token: String): ArtistItem? {
        return repository.getArtistInfo(artistId, token)
    }


    fun loadArtistTopTracks(artistId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val token = repository.getValidToken() ?: return@launch

            val artistInfo = repository.getArtistInfo(artistId, token)
            _selectedArtist.value = artistInfo

            val songs = repository.getTopTracksForArtist(artistId, token)
            _artistSongs.value = songs
            _isLoading.value = false
        }
    }

    fun loadMoreSongsForArtist(artistId: String) {
        viewModelScope.launch {
            if (_isLoading.value) return@launch

            _isLoading.value = true
            val token = repository.getValidToken() ?: return@launch

            val newSongs = repository.getTopTracksForArtist(
                artistId = artistId,
                token = token,
                offset = currentArtistOffset,
                limit = artistBatchSize
            )

            _artistSongs.value = _artistSongs.value + newSongs
            currentArtistOffset += artistBatchSize
            _isLoading.value = false
        }
    }


    fun loadPersonalizedSections() {
        Log.d("PersonalizedSections", "Cargando secciones personalizadas...")

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val artistSections = mutableListOf<ContentSection>()
                val genreSections = mutableListOf<ContentSection>()

                val favorites = melodixRepository.getFavoriteSongs()
                val token = repository.getValidToken() ?: return@launch
                val favoriteIds = favorites.map { it.id }.toSet()

                val topArtists = favorites
                    .map { it.artistId }
                    .distinct()
                    .shuffled()
                    .take(3)

                for (artistId in topArtists) {
                    val offset = (0..5).random()
                    delay(5000)
                    val topTracks = repository.getTopTracksForArtist(artistId, token, offset = offset)
                        .filter { it.id !in favoriteIds }

                    if (topTracks.isNotEmpty()) {
                        val section = ContentSection(
                            title = "Para ti: ${topTracks.first().artist}",
                            items = topTracks.map { ContentItem.SongItem(it) }
                        )
                        artistSections.add(section)
                    }
                }


                // -------- Secciones por Género --------
                val selectedGenres = repository.getGenresFromFavoriteSongs(favorites)

                for (genre in selectedGenres) {
                    val searchUrl = "https://api.spotify.com/v1/search?q=genre:$genre&type=track&market=ES&limit=10"
                    delay(5000)
                    val tracks = repository.spotifyApiService.searchTracksFromUrl(searchUrl, "Bearer $token").tracks.items
                        //.filter { it.id !in favoriteIds }

                    val songs = tracks.map { track ->
                        Song(
                            id = track.id,
                            title = track.name,
                            artist = track.artists.firstOrNull()?.name ?: "Desconocido",
                            artistId = track.artists.firstOrNull()?.id ?: "",
                            artistImageUrl = "", // Puedes mejorarlo si quieres
                            imageUrl = track.album.images.firstOrNull()?.url ?: "",
                            previewUrl = track.preview_url,
                            requestUrl = searchUrl
                        )
                    }

                    if (songs.isNotEmpty()) {
                        val section = ContentSection(
                            title = "Para ti: $genre",
                            items = songs.map { ContentItem.SongItem(it) }
                        )
                        genreSections.add(section)
                    }
                }

                Log.d("DEBUG", "Favoritas: ${favorites.size}")
                Log.d("DEBUG", "Géneros detectados: $selectedGenres")
                Log.d("DEBUG", "Artistas favoritos detectados: $topArtists")


                val normalSections = repository.getMultipleSections(batchSize)
                _contentSections.value = interleaveMultipleSections(artistSections, genreSections, normalSections)

                Log.d("DEBUG", "Intercalando: artistas=${artistSections.size}, géneros=${genreSections.size}, normales=${normalSections.size}")

            } catch (e: Exception) {
                Log.e("ViewModel", "Error cargando secciones personalizadas: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun interleaveMultipleSections(vararg sectionsLists: List<ContentSection>): List<ContentSection> {
        val result = mutableListOf<ContentSection>()
        val maxSize = sectionsLists.maxOfOrNull { it.size } ?: 0

        for (i in 0 until maxSize) {
            for (list in sectionsLists) {
                if (i < list.size) result.add(list[i])
            }
        }
        return result
    }
}


