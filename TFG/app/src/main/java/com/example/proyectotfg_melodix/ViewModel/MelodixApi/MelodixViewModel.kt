package com.example.proyectotfg_melodix.ViewModel.MelodixApi

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectotfg_melodix.Model.MelodixApi.Playlist
import com.example.proyectotfg_melodix.Model.MelodixApi.PlaylistRequest
import com.example.proyectotfg_melodix.Model.MelodixApi.PlaylistWithSongs
import com.example.proyectotfg_melodix.Model.MelodixApi.SongInPlaylist
import com.example.proyectotfg_melodix.Retrofit.MelodixApi.MelodixRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.proyectotfg_melodix.Model.Request.ArtistItem
import com.example.proyectotfg_melodix.Model.Request.Song
import kotlinx.coroutines.flow.update

class MelodixViewModel(
    private val melodixRepository: MelodixRepository) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    //Para gestionar los favoritos
    private val _favoriteArtists = MutableStateFlow<List<ArtistItem>>(emptyList())
    val favoriteArtists: StateFlow<List<ArtistItem>> = _favoriteArtists

    private val _selectedArtistFavorite = MutableStateFlow(false)
    val selectedArtistFavorite: StateFlow<Boolean> = _selectedArtistFavorite

   //Para gestionar canciones favoritas
   private val _favoriteSongs = MutableStateFlow<List<Song>>(emptyList())
    val favoriteSongs: StateFlow<List<Song>> = _favoriteSongs

    private val _currentSongIsFavorite = MutableStateFlow(false)
    val currentSongIsFavorite: StateFlow<Boolean> = _currentSongIsFavorite

    //Para gestionar playlists
    private val _playlists = MutableStateFlow<List<PlaylistWithSongs>>(emptyList())
    val playlists: StateFlow<List<PlaylistWithSongs>> = _playlists

    private val _selectedPlaylist = MutableStateFlow<PlaylistWithSongs?>(null)
    val selectedPlaylist: StateFlow<PlaylistWithSongs?> = _selectedPlaylist


//Funciones para manejar la gestión de artistas favoritos
    fun loadFavoriteArtists() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _favoriteArtists.value = melodixRepository.getFavoriteArtists()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun checkIfFavorite(artistId: String) {
        viewModelScope.launch {
            val favorites = melodixRepository.getFavoriteArtists()
            val isFavorite = favorites.any { it.id == artistId }
            _selectedArtistFavorite.value = isFavorite
        }
    }

    fun toggleFavorite(artist: ArtistItem) {
        viewModelScope.launch {
            val isFavorite = _favoriteArtists.value.any { it.id == artist.id }

            if (isFavorite) {
                val favorites = melodixRepository.getFavoriteArtists()
                val favoriteArtist = favorites.find { it.id == artist.id }
                val success = melodixRepository.removeFavoriteArtist(favoriteArtist?.idApi ?: return@launch)
                if (success) {
                    _favoriteArtists.value = _favoriteArtists.value.filterNot { it.id == artist.id }
                    _selectedArtistFavorite.value = false
                }
            } else {
                val success = melodixRepository.addFavoriteArtist(artist)
                if (success) {
                    _favoriteArtists.value = _favoriteArtists.value + artist
                    _selectedArtistFavorite.value = true
                }
            }
        }
    }

//Funciones para gestionar las canciones favoritas
fun loadFavoriteSongs() {
    viewModelScope.launch {
        try {
            _favoriteSongs.value = melodixRepository.getFavoriteSongs()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
    fun toggleFavoriteSong(song: Song) {
        viewModelScope.launch {
            val favorites = melodixRepository.getFavoriteSongs()
            val songFromApi = favorites.find { it.id == song.id }
            val isAlreadyFavorite = songFromApi != null

            if (isAlreadyFavorite) {
                val success = melodixRepository.removeSongAndFromFavorites(songFromApi!!)
                if (success) {
                    _favoriteSongs.value = _favoriteSongs.value.filterNot { it.id == song.id }
                }
            } else {
                val savedSong = melodixRepository.saveSongAndAddToFavorites(song)
                if (savedSong != null) {
                    _favoriteSongs.value = _favoriteSongs.value + savedSong
                }
            }
        }
    }
    fun checkIfSongIsFavorite(spotifyId: String) {
        viewModelScope.launch {
            val favorites = melodixRepository.getFavoriteSongs()
            _currentSongIsFavorite.value = favorites.any { it.id == spotifyId }
        }
    }
    fun toggleFavoriteInPlayer(song: Song) {
        viewModelScope.launch {
            val favorites = melodixRepository.getFavoriteSongs()
            val songFromApi = favorites.find { it.id == song.id }

            val isFavorite = currentSongIsFavorite.value
            val success = if (isFavorite) {
                // Usar el ID de la relación favorita para eliminar correctamente
                if (songFromApi?.idFavorite != null && songFromApi.idApi != null) {
                    melodixRepository.removeSongAndFromFavorites(songFromApi)
                } else false
            } else {
                melodixRepository.saveSongAndAddToFavorites(song) != null
            }
            if (success) {
                _currentSongIsFavorite.value = !isFavorite
            }
        }
    }

//Funciones para las playlist
// Cargar todas las playlists con sus canciones
fun loadPlaylists() {
    viewModelScope.launch {
        Log.d("MelodixVM", "→ Llamando a getAllPlaylists()")
        val result = melodixRepository.getAllPlaylists()
        Log.d("MelodixVM", "✓ Playlists recibidas: ${result.size}")
        _playlists.value = result
    }
}


    fun createPlaylist(name: String, description: String) {
        viewModelScope.launch {
            val success = melodixRepository.createPlaylist(PlaylistRequest(name = name, description = description))
            if (success) loadPlaylists()
        }
    }

    fun addSongToPlaylist(playlistId: Int, song: Song) {
        viewModelScope.launch {
            val success = melodixRepository.addSongToPlaylist(playlistId, song.idApi ?: return@launch)
            if (success) {
                _selectedPlaylist.update { current ->
                    current?.copy(
                        songs = current.songs + SongInPlaylist(
                            playlistSongId = -1, // Puedes usar -1 temporalmente si aún no tienes el ID
                            song = song
                        )
                    )
                }
            }
            loadPlaylistById(playlistId)
        }
    }
    fun addCurrentSongToPlaylist(playlistId: Int, song: Song) {
        viewModelScope.launch {
            // Obtener todas las canciones y buscar si ya existe
            val allSongs = melodixRepository.getAllSongs()
            val existing = allSongs.find { it.id == song.id }

            val songApiId = existing?.idApi ?: run {
                val saved = melodixRepository.saveSong(song)
                saved?.idApi ?: return@launch
            }

            val success = melodixRepository.addSongToPlaylist(playlistId, songApiId)
            if (success) {
                val updated = melodixRepository.getPlaylistWithSongsById(playlistId)
                _playlists.update { playlists ->
                    playlists.map {
                        if (it.id == playlistId && updated != null) updated else it
                    }
                }

                _selectedPlaylist.update {
                    if (it?.id == playlistId && updated != null) updated else it
                }
            }
        }
    }
    fun removeSongFromPlaylist(playlistSongId: Int, songIdApi: String) {
        viewModelScope.launch {
            val success = melodixRepository.removeSongFromPlaylist(playlistSongId, songIdApi)
            if (success) {
                _selectedPlaylist.update { current ->
                    current?.copy(
                        songs = current.songs.filterNot { it.playlistSongId == playlistSongId }
                    )
                }
            }
        }
    }
    fun loadPlaylistById(playlistId: Int) {
        viewModelScope.launch {
            val playlist = melodixRepository.getPlaylistWithSongsById(playlistId)
            _selectedPlaylist.value = playlist
        }
    }

    fun deletePlaylist(playlistId: Int) {
        viewModelScope.launch {
            val success = melodixRepository.deletePlaylist(playlistId)
            if (success) {
                _playlists.update { list -> list.filterNot { it.id == playlistId } }
            }
        }
    }
}
