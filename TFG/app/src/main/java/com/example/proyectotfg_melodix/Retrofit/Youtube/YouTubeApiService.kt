package com.example.proyectotfg_melodix.Retrofit.Youtube

import com.example.proyectotfg_melodix.Model.Youtube.YouTubeSearchResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface YouTubeApiService {
    @GET("search")
    suspend fun searchVideos(
        @Query("part") part: String = "snippet",
        @Query("q") query: String,
        @Query("type") type: String = "video",
        @Query("key") apiKey: String,
        @Query("maxResults") maxResults: Int = 1
    ): YouTubeSearchResponse

    companion object {
        fun create(): YouTubeApiService {
            return Retrofit.Builder()
                .baseUrl("https://www.googleapis.com/youtube/v3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(YouTubeApiService::class.java)
        }
    }
}
