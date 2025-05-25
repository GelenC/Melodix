package com.example.proyectotfg_melodix.Retrofit.Youtube

import android.util.Log
import com.example.proyectotfg_melodix.Model.Request.ArtistItem
import com.example.proyectotfg_melodix.Model.Youtube.YoutubeSearch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class YouTubeRepository(private val apiKey: String) {
    private val service = YouTubeApiService.create()

    suspend fun searchFirstVideoId(query: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val response = service.searchVideos(query = query, apiKey = apiKey)
                response.items.firstOrNull()?.id?.videoId
            } catch (e: Exception) {
                Log.e("YouTubeRepository", "Error al buscar en YouTube: ${e.message}")
                null
            }
        }
    }

    suspend fun searchVideos(query: String): List<YoutubeSearch> {
        return withContext(Dispatchers.IO) {
            val items = mutableListOf<YoutubeSearch>()

            try {
                val url = URL("https://www.googleapis.com/youtube/v3/search?part=snippet&q=${query}&type=video&maxResults=10&key=$apiKey")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val responseText = connection.inputStream.bufferedReader().use { it.readText() }
                    val json = JSONObject(responseText)
                    val jsonItems = json.getJSONArray("items")

                    for (i in 0 until jsonItems.length()) {
                        val item = jsonItems.getJSONObject(i)
                        val id = item.getJSONObject("id").getString("videoId")
                        val snippet = item.getJSONObject("snippet")
                        val title = snippet.getString("title")
                        val channelTitle = snippet.getString("channelTitle")
                        val thumbnailUrl = snippet.getJSONObject("thumbnails")
                            .getJSONObject("default")
                            .getString("url")

                        items.add(
                            YoutubeSearch(
                                videoId = id,
                                title = title,
                                channelTitle = channelTitle,
                                thumbnailUrl = thumbnailUrl
                            )
                        )
                    }
                } else {
                    Log.e("YouTubeRepository", "Error: ${connection.responseCode}")
                }

                connection.disconnect()
            } catch (e: Exception) {
                Log.e("YouTubeRepository", "Excepción al buscar videos: ${e.message}")
            }

            items
        }
    }

}
