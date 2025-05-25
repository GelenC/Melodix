package com.example.proyectotfg_melodix.Retrofit.MelodixApi
import android.util.Log
import com.example.proyectotfg_melodix.Model.MelodixApi.FavoriteArtistApiRequest
import com.example.proyectotfg_melodix.Model.MelodixApi.FavoriteSongRequest
import com.example.proyectotfg_melodix.Model.MelodixApi.Playlist
import com.example.proyectotfg_melodix.Model.MelodixApi.PlaylistRequest
import com.example.proyectotfg_melodix.Model.MelodixApi.PlaylistSongRequest
import com.example.proyectotfg_melodix.Model.MelodixApi.PlaylistWithSongs
import com.example.proyectotfg_melodix.Model.MelodixApi.SongInPlaylist
import com.example.proyectotfg_melodix.Model.MelodixApi.SongRequest
import com.example.proyectotfg_melodix.Model.Request.ArtistItem
import com.example.proyectotfg_melodix.Model.Request.Image
import com.example.proyectotfg_melodix.Model.Request.Song
import retrofit2.HttpException

class MelodixRepository(
    private val apiService: MelodixApiService) {

//Metodos para los artistas favoritos
    suspend fun getFavoriteArtists(): List<ArtistItem> {
        val response = apiService.getFavoriteArtists()
        return response.map {
            ArtistItem(
                id = it.spotifyArtistId,
                name = it.name,
                images = listOf(Image(it.imageUrl)),
                idApi = it.id
            )
        }
    }
    suspend fun addFavoriteArtist(artist: ArtistItem): Boolean {
        return try {
            val request = FavoriteArtistApiRequest(
                spotifyArtistId = artist.id,
                name = artist.name,
                imageUrl = artist.images.firstOrNull()?.url ?: ""
            )
            apiService.addFavoriteArtist(request)
            true
        } catch (e: Exception) {
            false
        }
    }
    suspend fun removeFavoriteArtist(artist: String): Boolean {
        return try {
            apiService.removeFavoriteArtist(artist)
            true
        } catch (e: Exception) {
            false
        }
    }

//Metodos para las canciones favoritas
suspend fun getAllSongs(): List<Song> {
    return try {
        apiService.getAllSongs().map { dto ->
            Song(
                id = dto.spotifySongId,
                title = dto.title,
                artist = dto.artistName,
                artistId = dto.artistId,
                artistImageUrl = "",
                imageUrl = dto.albumImageUrl,
                previewUrl = dto.previewUrl,
                requestUrl = dto.requestUrl,
                isDownloaded = false,
                localPath = null,
                idApi = dto.id
            )
        }
    } catch (e: Exception) {
        emptyList()
    }
}

    suspend fun getFavoriteSongs(): List<Song> {
    return try {
        apiService.getFavoriteSongs().map { response ->
            val songData = response.song
            Song(
                id = songData.spotifySongId,
                title = songData.title,
                artist = songData.artistName,
                artistId = songData.artistId,
                artistImageUrl = "",
                imageUrl = songData.albumImageUrl,
                previewUrl = songData.previewUrl,
                requestUrl = songData.requestUrl,
                isDownloaded = false,
                localPath = null,
                idApi = songData.id,            // UUID en la tabla songs
                idFavorite = response.id        // ID en la tabla de favoritos
            )
        }
    } catch (e: Exception) {
        emptyList()
    }
}
    suspend fun saveSong(song: Song): Song? {
        return try {
            val songRequest = SongRequest(
                spotifySongId = song.id,
                title = song.title,
                artistName = song.artist,
                artistId = song.artistId,
                albumImageUrl = song.imageUrl,
                previewUrl = song.previewUrl ?: "",
                requestUrl = song.requestUrl
            )
            val response = apiService.saveSong(songRequest)
            val songApiId = response.body()?.id ?: return null

            song.copy(idApi = songApiId)
        } catch (e: Exception) {
            null
        }
    }
    suspend fun saveSongAndAddToFavorites(song: Song): Song? {
        return try {
            val allSongs = apiService.getAllSongs()
            val existingSong = allSongs.find { it.spotifySongId == song.id }

            val songApiId = if (existingSong != null) {
                existingSong.id
            } else {
                val songRequest = SongRequest(
                    spotifySongId = song.id,
                    title = song.title,
                    artistName = song.artist,
                    artistId = song.artistId,
                    albumImageUrl = song.imageUrl,
                    previewUrl = song.previewUrl ?: "",
                    requestUrl = song.requestUrl
                )
                val response = apiService.saveSong(songRequest)
                response.body()?.id ?: return null
            }

            apiService.addFavoriteSong(FavoriteSongRequest(songApiId))
            song.copy(idApi = songApiId)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun removeSongAndFromFavorites(song: Song): Boolean {
        return try {
            // Validar que ambos IDs estén presentes
            val favoriteId = song.idFavorite ?: return false
            val songApiId = song.idApi ?: return false
            // 1. Eliminar de favoritos
            apiService.removeFavoriteSong(favoriteId)
            // 2. Eliminar de tabla songs
            apiService.deleteSong(songApiId)
            true
        } catch (e: Exception) {
            Log.e("MelodixRepo", "Error al eliminar canción y favorito", e)
            false
        }
    }

//Listas de Reproducción
// Obtener todas las playlists con sus canciones
suspend fun getAllPlaylists(): List<PlaylistWithSongs> {
    return try {
        val playlists = apiService.getPlaylists()
        val allPlaylistSongs = apiService.getAllPlaylistSongs()

        playlists.map { playlist ->
            val songsForThisPlaylist = allPlaylistSongs.filter { it.playlist.id == playlist.id }

            val songs = songsForThisPlaylist.map { response ->
                val dto = response.song
                SongInPlaylist(
                    playlistSongId = response.id,
                    song = Song(
                        id = dto.spotifySongId,
                        title = dto.title,
                        artist = dto.artistName,
                        artistId = dto.artistId,
                        artistImageUrl = "",
                        imageUrl = dto.albumImageUrl,
                        previewUrl = dto.previewUrl,
                        requestUrl = dto.requestUrl,
                        isDownloaded = false,
                        localPath = null,
                        idApi = dto.id
                    )
                )
            }

            PlaylistWithSongs(
                id = playlist.id,
                name = playlist.name,
                description = playlist.description,
                songs = songs
            )
        }
    } catch (e: Exception) {
        Log.e("MelodixRepo", "✘ Error al obtener playlists", e)
        emptyList()
    }
}


    // Crear nueva playlist
    suspend fun createPlaylist(playlist: PlaylistRequest): Boolean {
        return try {
            val response = apiService.createPlaylist(playlist)

            if (!response.isSuccessful) {
                Log.e("MelodixRepo", "Error creando playlist: ${response.errorBody()?.string()}")
            }

            response.isSuccessful
        } catch (e: Exception) {
            Log.e("MelodixRepo", " Error creando playlist", e)
            false
        }
    }


    // Agregar canción a playlist
    suspend fun addSongToPlaylist(playlistId: Int, songId: String): Boolean {
        return try {
            val request = PlaylistSongRequest(playlistId, songId)
            apiService.addSongToPlaylist(request)
            true
        } catch (e: Exception) {
            Log.e("MelodixRepo", "✘ Error al añadir canción a playlist", e)
            false
        }
    }

    // Eliminar canción de playlist
    suspend fun removeSongFromPlaylist(playlistSongId: Int, songIdApi: String): Boolean {
        return try {
            apiService.removeSongFromPlaylist(playlistSongId)
            // Intentar eliminar la canción
            try {
                apiService.deleteSong(songIdApi)
                Log.d("MelodixRepo", "✓ Canción eliminada de tabla songs")
            } catch (e: HttpException) {
                if (e.code() == 409) {
                    Log.d("MelodixRepo", " Canción no eliminada: está en uso (favoritos o otra playlist)")
                } else {
                    throw e
                }
            }
            true
        } catch (e: Exception) {
            Log.e("MelodixRepo", "✘ Error al eliminar canción de playlist", e)
            false
        }
    }


    suspend fun getPlaylistWithSongsById(playlistId: Int): PlaylistWithSongs? {
        return try {
            val all = apiService.getAllPlaylistSongs()
            val songsForThisPlaylist = all.filter { it.playlist.id == playlistId }

            if (songsForThisPlaylist.isEmpty()) return null

            val playlist = songsForThisPlaylist.first().playlist
            val songs = songsForThisPlaylist.map { songEntry ->
                val dto = songEntry.song
                SongInPlaylist(
                    playlistSongId = songEntry.id,
                    song = Song(
                        id = dto.spotifySongId,
                        title = dto.title,
                        artist = dto.artistName,
                        artistId = dto.artistId,
                        artistImageUrl = "",
                        imageUrl = dto.albumImageUrl,
                        previewUrl = dto.previewUrl,
                        requestUrl = dto.requestUrl,
                        isDownloaded = false,
                        localPath = null,
                        idApi = dto.id
                    )
                )
            }

            PlaylistWithSongs(
                id = playlist.id,
                name = playlist.name,
                description = playlist.description,
                songs = songs
            )
        } catch (e: Exception) {
            null
        }
    }

    suspend fun deletePlaylist(playlistId: Int): Boolean {
        return try {
            // Paso 1: eliminar relaciones PlaylistSong de esta playlist
            val allRelations = apiService.getAllPlaylistSongs()
            val relationsToDelete = allRelations.filter { it.playlist.id == playlistId }

            for (relation in relationsToDelete) {
                apiService.removeSongFromPlaylist(relation.id)
            }

            // Paso 2: eliminar la playlist
            apiService.deletePlaylist(playlistId)

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


}
