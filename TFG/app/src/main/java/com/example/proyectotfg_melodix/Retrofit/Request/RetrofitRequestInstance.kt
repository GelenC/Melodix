package com.example.proyectotfg_melodix.Retrofit.Request

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitRequestInstance {

      fun create(context: Context): SpotifyApiService {
            val client = OkHttpClient.Builder()
                .addInterceptor(SpotifyAuthInterceptor(context)) // Añadir el token
                .build()

            return Retrofit.Builder()
                .baseUrl("https://api.spotify.com/") // Base común
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
                .create(SpotifyApiService::class.java)
        }
}