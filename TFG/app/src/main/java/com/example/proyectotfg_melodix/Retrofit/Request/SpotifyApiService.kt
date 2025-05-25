package com.example.proyectotfg_melodix.Retrofit.Request

import com.example.proyectotfg_melodix.Model.Request.ArtistGenresResponse
import com.example.proyectotfg_melodix.Model.Request.ArtistItem
import com.example.proyectotfg_melodix.Model.Request.ArtistSearchResponse
import com.example.proyectotfg_melodix.Model.Request.ContentItem
import com.example.proyectotfg_melodix.Model.Request.GenreCategoryResponse
import com.example.proyectotfg_melodix.Model.Request.TrackResponse
import com.example.proyectotfg_melodix.Model.Request.TrackSearchResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface SpotifyApiService {

    // Búsqueda de artistas por género
    @GET("/v1/search")
    suspend fun searchArtistsByGenre(
        @Header("Authorization") authHeader: String,
        @Query("q") genreQuery: String = "genre:pop",
        @Query("type") type: String = "artist",
        @Query("limit") limit: Int = 10,
        @Query("offset") offset: Int = 0
    ): ArtistSearchResponse


    // Top tracks de un artista (responde con objeto tracks)
    @GET
    suspend fun getTopTracksFromUrl(
        @Url url: String,
        @Header("Authorization") authHeader: String
    ): TrackResponse

    // Búsqueda de canciones por género (responde con objeto 'tracks' > items)
    @GET
    suspend fun searchTracksFromUrl(
        @Url url: String,
        @Header("Authorization") authHeader: String
    ): TrackSearchResponse

    //Busqueda de géneros populares
    @GET("/v1/browse/categories")
    suspend fun getPopularGenres(
        @Header("Authorization") authHeader: String,
        @Query("limit") limit: Int = 10,
        @Query("offset") offset: Int = 0
    ): GenreCategoryResponse

    //Buscar información de artista por id
    @GET("v1/artists/{id}")
    suspend fun getArtistById(
        @Header("Authorization") authHeader: String,
        @Path("id") artistId: String
    ): ArtistItem

    //Point duplicado solo para los géneros(recomendaciones)
    @GET("v1/artists/{id}")
    suspend fun getArtistGenresById(
        @Header("Authorization") authHeader: String,
        @Path("id") artistId: String
    ): ArtistGenresResponse

    //Buscar artistas por nombre
    @GET("v1/search")
    suspend fun searchArtists(
        @Query("q") query: String,
        @Query("type") type: String = "artist",
        @Query("limit") limit: Int = 4,
        @Header("Authorization") token: String
    ): ArtistSearchResponse


    companion object {
        fun create(): SpotifyApiService {
            return Retrofit.Builder()
                .baseUrl("https://api.spotify.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(SpotifyApiService::class.java)
        }
    }
}
