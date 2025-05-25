package com.example.proyectotfg_melodix.Retrofit.MelodixApi

import com.example.proyectotfg_melodix.Model.MelodixApi.AddFavoriteSongRequest
import com.example.proyectotfg_melodix.Model.MelodixApi.FavoriteArtistApiRequest
import com.example.proyectotfg_melodix.Model.MelodixApi.FavoriteArtistApiResponse
import com.example.proyectotfg_melodix.Model.MelodixApi.FavoriteSongRequest
import com.example.proyectotfg_melodix.Model.MelodixApi.FavoriteSongResponse
import com.example.proyectotfg_melodix.Model.MelodixApi.Playlist
import com.example.proyectotfg_melodix.Model.MelodixApi.PlaylistRequest
import com.example.proyectotfg_melodix.Model.MelodixApi.PlaylistSongRequest
import com.example.proyectotfg_melodix.Model.MelodixApi.PlaylistSongResponse
import com.example.proyectotfg_melodix.Model.MelodixApi.SongApiResponse
import com.example.proyectotfg_melodix.Model.MelodixApi.SongDto
import com.example.proyectotfg_melodix.Model.MelodixApi.SongRequest
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MelodixApiService {

//Solicitudes de Artistas Favoritos
    @GET("favorites/artists")
    suspend fun getFavoriteArtists(): List<FavoriteArtistApiResponse>
    @POST("favorites/artists")
    suspend fun addFavoriteArtist(@Body artist: FavoriteArtistApiRequest)
    @DELETE("favorites/artists/{id}")
    suspend fun removeFavoriteArtist(@Path("id") idApi: String)

//Solicitudes de Canciones favoritas
    @GET("songs")
    suspend fun getAllSongs(): List<SongDto>
    @GET("favorites/songs")
    suspend fun getFavoriteSongs(): List<FavoriteSongResponse>
    @POST("favorites/songs")
    suspend fun addFavoriteSong(@Body request: FavoriteSongRequest): Response<Unit>
    @DELETE("favorites/songs/{favoriteId}")
    suspend fun removeFavoriteSong(@Path("favoriteId") id: Long): Response<Unit>
    @POST("songs")
    suspend fun saveSong(@Body song: SongRequest): Response<SongApiResponse>
    @DELETE("songs/{id}")
    suspend fun deleteSong(@Path("id") songId: String): Response<Unit>

//Solicitudes de Playlists
    @GET("playlists")
    suspend fun getPlaylists(): List<Playlist>
    @POST("playlists")
    suspend fun createPlaylist(@Body playlist: PlaylistRequest): Response<Playlist>
    @DELETE("playlists/{id}")
    suspend fun deletePlaylist(@Path("id") playlistId: Int): Response<Unit>
    @GET("playlistsongs")
    suspend fun getAllPlaylistSongs(): List<PlaylistSongResponse>
    @POST("playlistsongs")
    suspend fun addSongToPlaylist(@Body request: PlaylistSongRequest): PlaylistSongResponse
    @DELETE("playlistsongs/{id}")
    suspend fun removeSongFromPlaylist(@Path("id") playlistSongId: Int): Response<Unit>



    companion object {
        fun create(): MelodixApiService {
            //http://192.168.1.136:8080/ para móvil
            //http://10.0.2.2:8080/  para emulador Android
            val retrofit = Retrofit.Builder()
                .baseUrl("http://192.168.1.136:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(MelodixApiService::class.java)
        }
    }
}
