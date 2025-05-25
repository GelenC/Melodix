package com.example.proyectotfg_melodix.Retrofit.Request

import android.net.Uri
import android.util.Log
import com.example.proyectotfg_melodix.Database.Spotify.RequestDao
import com.example.proyectotfg_melodix.Database.Spotify.RequestEntity
import com.example.proyectotfg_melodix.Database.Spotify.TokenEntity
import com.example.proyectotfg_melodix.Database.Spotify.TokenRepository
import com.example.proyectotfg_melodix.Model.Request.ArtistItem
import com.example.proyectotfg_melodix.Model.Request.ArtistSearchResponse
import com.example.proyectotfg_melodix.Model.Request.ContentItem
import com.example.proyectotfg_melodix.Model.Request.ContentSection
import com.example.proyectotfg_melodix.Model.Request.GenreCategoryResponse
import com.example.proyectotfg_melodix.Model.Request.Image
import com.example.proyectotfg_melodix.Model.Request.Song
import com.example.proyectotfg_melodix.Model.Request.Track
import com.example.proyectotfg_melodix.Retrofit.Token.SpotifyAuthManager

class SpotifyRepository constructor(
    private val tokenRepository: TokenRepository,
    private val requestDao: RequestDao,
    val spotifyApiService: SpotifyApiService
) {

    suspend fun getMultipleSections(count: Int = 10): List<ContentSection> {
        val sections = mutableListOf<ContentSection>()
        val maxAttempts = count * 6

        var attempts = 0
        while (sections.size < count && attempts < maxAttempts) {
            val section = getRandomContentSection()
            if (section != null && section.items.isNotEmpty()) {
                sections.add(section)
                Log.d("SectionDebug", "sección agregada: ${section.title}")
            } else {
                Log.d("SectionDebug", "Sección vacía o nula")
            }
            attempts++
        }

        return sections
    }



    suspend fun getRandomContentSection(): ContentSection? {
        val secondaryRequests = requestDao.getRequestsByType("SECONDARY")
        if (secondaryRequests.isEmpty()) {
            Log.e("SpotifyRepository", "No hay solicitudes secundarias disponibles")
            return null
        }

        val secondary = secondaryRequests.random()
        val relatedPrimaries = requestDao.getPrimaryRequestsBySecondary(secondary.id)

        if (relatedPrimaries.isNotEmpty()) {
            val primary = relatedPrimaries.random()
            val token = tokenRepository.getToken() ?: return null
            val authHeader = "Bearer ${token.accessToken}"

            val temporaryUrl = resolveUrl2(primary, secondary, authHeader)
            val items = fetchContentItemsFromSpotify(primary, secondary, temporaryUrl)
            if (items.isNotEmpty()) {
                return ContentSection(title = primary.name, items = items)
            }
        }

        val fallbackItems = fetchContentItemsFromSpotify(secondary, secondary, secondary.url)
        if (fallbackItems.isNotEmpty()) {
            return ContentSection(title = secondary.name, items = fallbackItems)
        }

        Log.e("SpotifyRepository", "No se pudieron obtener resultados para ${secondary.id}")
        return null
    }

    private suspend fun resolveUrl2(
        primary: RequestEntity,
        secondary: RequestEntity,
        authHeader: String
    ): String {
        return when (secondary.id) {
            "top_artists_genre" -> {
                val genreResponse = spotifyApiService.getPopularGenres(authHeader)
                val genres = genreResponse.categories.items.map { it.name }

                if (genres.isEmpty()) {
                    Log.e("SpotifyRepository", "No se encontraron géneros populares")
                    return primary.url
                }

                val selectedGenre = genres.random().lowercase()
                val offset = (0..40).random()

                val artistResponse = spotifyApiService.searchArtistsByGenre(
                    authHeader = authHeader,
                    genreQuery = "genre:$selectedGenre",
                    offset = offset
                )

                return when (primary.id) {
                    "top_tracks_10_artists" -> {
                        secondary.url.replace("genre:pop", "genre:$selectedGenre")
                    }
                    "top_tracks_artist" -> {
                        val artistId = artistResponse.artists.items.randomOrNull()?.id ?: return primary.url
                        primary.url.replace("{artist_id}", artistId)
                    }
                    else -> {
                        Log.e("SpotifyRepository", "Solicitud primaria no registrada para esta secundaria")
                        primary.url
                    }
                }
            }

            "genres_popular" -> secondary.url
            else -> secondary.url
        }
    }

    private suspend fun fetchContentItemsFromSpotify(
        primary: RequestEntity,
        secondary: RequestEntity,
        temporaryUrl: String
    ): List<ContentItem> {

        val token = tokenRepository.getToken()
        if (token?.accessToken.isNullOrBlank()) {
            Log.e("SpotifyRepository", "Token no disponible")
            return emptyList()
        }

        val authHeader = "Bearer ${token?.accessToken}"
        Log.d("AuthDebug", "Token usado: $authHeader")
        Log.d("AuthDebug", "URL usada: $temporaryUrl")

        return try {
            when (primary.id) {
                "top_tracks_artist" -> {
                    val response = spotifyApiService.getTopTracksFromUrl(temporaryUrl, authHeader)
                    val rawToken = token?.accessToken

                    response.tracks.map { track ->
                        val artistId = track.artists.firstOrNull()?.id ?: ""
                        val artistImageUrl = rawToken?.let { getArtistImageUrl(artistId, it) }

                        ContentItem.SongItem(
                            Song(
                                id = track.id,
                                title = track.name,
                                artist = track.artists.firstOrNull()?.name ?: "Desconocido",
                                artistId = track.artists.firstOrNull()?.id ?: "",
                                artistImageUrl = artistImageUrl!!,
                                imageUrl = track.album.images.firstOrNull()?.url ?: "",
                                previewUrl = track.preview_url,
                                requestUrl = primary.url
                            )
                        )
                    }
                }

                "top_tracks_10_artists" -> {
                    val genre = temporaryUrl.substringAfter("genre:").substringBefore("&")
                    val offset = (0..40).random()

                    val artistResponse = spotifyApiService.searchArtistsByGenre(
                        authHeader = authHeader,
                        genreQuery = "genre:$genre",
                        offset = offset
                    )

                    val artistList = artistResponse.artists.items.shuffled().take(10)
                    val songs = mutableListOf<ContentItem>()

                    for (artist in artistList) {
                        val artistTopTracksUrl = primary.url.replace("{artist_id}", artist.id)
                        val trackResponse = spotifyApiService.getTopTracksFromUrl(artistTopTracksUrl, authHeader)
                        val randomTrack = trackResponse.tracks.randomOrNull()


                        randomTrack?.let { track ->
                            songs.add(
                                ContentItem.SongItem(
                                    Song(
                                        id = track.id,
                                        title = track.name,
                                        artist = artist.name,
                                        artistId = artist.id,
                                        artistImageUrl = artist.images.firstOrNull()?.url ?: "",
                                        imageUrl = track.album.images.firstOrNull()?.url ?: "",
                                        previewUrl = track.preview_url,
                                        requestUrl = artistTopTracksUrl
                                    )
                                )
                            )
                        }
                    }

                    songs
                }

                "top_tracks_genre" -> {
                    val offset = (0..40).random()
                    val genreResponse = spotifyApiService.getPopularGenres(authHeader, offset)
                    val genreNames = genreResponse.categories.items.map { it.name }
                    val selectedGenre = genreNames.randomOrNull() ?: "pop"

                    val finalUrl = primary.url.replace("genre:pop", "genre:$selectedGenre")
                    val response = spotifyApiService.searchTracksFromUrl(finalUrl, authHeader)
                    val rawToken = token?.accessToken

                    response.tracks.items.map { track ->
                        val artistId = track.artists.firstOrNull()?.id ?: ""
                        val artistImageUrl = rawToken?.let { getArtistImageUrl(artistId, it) }

                        ContentItem.SongItem(
                            Song(
                                id = track.id,
                                title = track.name,
                                artist = track.artists.firstOrNull()?.name ?: "Desconocido",
                                artistId = track.artists.firstOrNull()?.id ?: "",
                                artistImageUrl = artistImageUrl!!,
                                imageUrl = track.album.images.firstOrNull()?.url ?: "",
                                previewUrl = track.preview_url,
                                requestUrl = finalUrl
                            )
                        )
                    }
                }

                "top_artists_genre" -> {
                val offset = (0..40).random()
                val genresList: GenreCategoryResponse = spotifyApiService.getPopularGenres(
                    authHeader = authHeader,
                    offset = offset
                )
                val genreNames = genresList.categories.items.map { it.name }
                val randomGenre = genreNames.randomOrNull() ?: "pop"

                val response: ArtistSearchResponse = spotifyApiService.searchArtistsByGenre(
                    authHeader = authHeader,
                    genreQuery = "genre:$randomGenre",
                    offset = offset
                )

                return response.artists.items.map { artist ->
                    ContentItem.ArtistItem(
                        artist = artist  // Aquí ya devuelve como ContentItem
                    )
                }
            }else -> {
                    if (primary.url.contains("search?", ignoreCase = true)) {
                        val response = spotifyApiService.searchTracksFromUrl(primary.url, authHeader)
                        response.tracks.items.map { track ->
                            ContentItem.SongItem(
                                Song(
                                    id = track.id,
                                    title = track.name,
                                    artist = track.artists.firstOrNull()?.name ?: "Desconocido",
                                    artistId = track.artists.firstOrNull()?.id ?: "",
                                    artistImageUrl = "",
                                    imageUrl = track.album.images.firstOrNull()?.url ?: "",
                                    previewUrl = track.preview_url,
                                    requestUrl = primary.url
                                )
                            )
                        }
                    } else {
                        val response = spotifyApiService.getTopTracksFromUrl(primary.url, authHeader)
                        response.tracks.map { track ->
                            ContentItem.SongItem(
                                Song(
                                    id = track.id,
                                    title = track.name,
                                    artist = track.artists.firstOrNull()?.name ?: "Desconocido",
                                    artistId = track.artists.firstOrNull()?.id ?: "",
                                    artistImageUrl = "",
                                    imageUrl = track.album.images.firstOrNull()?.url ?: "",
                                    previewUrl = track.preview_url,
                                    requestUrl = primary.url
                                )
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("SpotifyRepository", "Error al obtener contenido: ${e.message}")
            emptyList()
        }
    }

    private suspend fun checkAndRenewToken() {
        if (tokenRepository.isTokenExpired()) {
            val newToken = SpotifyAuthManager.getToken(
                clientId = "f8b9b5d050434dca90f70b5b8190f759",
                clientSecret = "b195ffe39114415b9406f70b94338c91"
            )

            newToken?.let {
                tokenRepository.saveToken(
                    TokenEntity(
                        accessToken = it.access_token,
                        tokenType = it.token_type,
                        expiresIn = it.expires_in,
                        timestamp = System.currentTimeMillis() / 1000
                    )
                )
            }
        }
    }

    suspend fun getValidToken(): String? {
        checkAndRenewToken()
        return tokenRepository.getToken()?.accessToken
    }

    suspend fun getTopTracksForArtist(artistId: String, token: String, offset: Int = 0, limit: Int = 10): List<Song> {
        return try {
            val url = "https://api.spotify.com/v1/artists/$artistId/top-tracks?market=ES"
            val artistImageUrl = getArtistImageUrl(artistId, token)
            val response = spotifyApiService.getTopTracksFromUrl(url, "Bearer $token")
            response.tracks
                .drop(offset) // Spotify no permite offset en esta petición, así que simulamos
                .take(limit)
                .map { track ->
                    Song(
                        id = track.id,
                        title = track.name,
                        artist = track.artists.firstOrNull()?.name ?: "Desconocido",
                        artistId = artistId,
                        artistImageUrl = artistImageUrl,
                        imageUrl = track.album.images.firstOrNull()?.url ?: "",
                        previewUrl = track.preview_url,
                        requestUrl = url
                    )
                }
        } catch (e: Exception) {
            Log.e("SpotifyRepository", "Error obteniendo canciones del artista: ${e.message}")
            emptyList()
        }
    }

    suspend fun getArtistInfo(artistId: String, token: String): ArtistItem? {
        return try {
            spotifyApiService.getArtistById("Bearer $token", artistId)
        } catch (e: Exception) {
            Log.e("SpotifyRepository", "Error obteniendo info del artista: ${e.message}")
            null
        }
    }

    suspend fun getArtistImageUrl(artistId: String, token: String): String {
        return try {
            val artist = spotifyApiService.getArtistById("Bearer $token", artistId)
            artist.images.firstOrNull()?.url ?: ""
        } catch (e: Exception) {
            Log.e("SpotifyRepository", "No se pudo obtener la imagen del artista: ${e.message}")
            ""
        }
    }

    //Para las busquedas
    suspend fun searchTrack(query: String, token: String): List<Track> {
        return try {
            val encodedQuery = Uri.encode(query) // muy importante codificar espacios y caracteres especiales
            val url = "https://api.spotify.com/v1/search?q=$encodedQuery&type=track&limit=1&market=ES"
            val response = spotifyApiService.searchTracksFromUrl(url, "Bearer $token")
            response.tracks.items
        } catch (e: Exception) {
            Log.e("SpotifyRepository", "Error buscando track: ${e.message}")
            emptyList()
        }
    }

    //Para buscar artistas
    suspend fun searchArtists(query: String, token: String): List<ArtistItem> {
        return try {
            val response = spotifyApiService.searchArtists(query, "artist", 4, "Bearer $token")
            val list = response.artists.items.map {
                ArtistItem(
                    id = it.id,
                    name = it.name,
                    images = it.images,
                    idApi = null
                )
            }
            Log.d("SearchDebug", "Artistas encontrados: ${list.map { it.name }}")
            list
        } catch (e: Exception) {
            Log.e("SearchDebug", "Error al buscar artistas: ${e.message}")
            emptyList()
        }
    }

    suspend fun getRecommendedTracksFromFavorites(favorites: List<Song>, token: String): List<Song> {
        val uniqueArtistIds = favorites.map { it.artistId }.distinct()
        val recommended = mutableListOf<Song>()

        for (artistId in uniqueArtistIds) {
            val topTracks = getTopTracksForArtist(artistId, token)
            recommended += topTracks.take(2) // o más si quieres
        }

        return recommended.shuffled()
    }

    suspend fun getGenresFromFavoriteSongs(favorites: List<Song>): List<String> {
        val token = getValidToken() ?: return emptyList()
        val genreCounts = mutableMapOf<String, Int>()

        for (song in favorites) {
            try {
                val genres = spotifyApiService.getArtistGenresById("Bearer $token", song.artistId).genres
                genres.forEach { genre ->
                    genreCounts[genre] = (genreCounts[genre] ?: 0) + 1
                }
            } catch (e: Exception) {
                Log.e("SpotifyRepository", "Error obteniendo géneros del artista ${song.artist}: ${e.message}")
            }
        }

        return genreCounts.entries
            .sortedByDescending { it.value }
            .map { it.key }
            .distinct()
            .shuffled()
            .take(3)
    }
}
