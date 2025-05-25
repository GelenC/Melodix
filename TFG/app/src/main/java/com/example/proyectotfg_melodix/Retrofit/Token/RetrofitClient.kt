package com.example.proyectotfg_melodix.Retrofit.Token

import android.util.Base64
import com.example.proyectotfg_melodix.Model.Token.TokenResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://accounts.spotify.com/"

    val authService: SpotifyAuthService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SpotifyAuthService::class.java)
    }
}

object SpotifyAuthManager {
    suspend fun getToken(clientId: String, clientSecret: String): TokenResponse? {
        val authHeader = "Basic " + Base64.encodeToString(
            "$clientId:$clientSecret".toByteArray(), Base64.NO_WRAP
        )

        return withContext(Dispatchers.IO) {
            val response = RetrofitClient.authService.getAccessToken(authHeader)
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        }
    }
}