package com.example.proyectotfg_melodix.Retrofit.Request

import android.content.Context
import com.example.proyectotfg_melodix.Database.Spotify.AppDatabase
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class SpotifyAuthInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val db = AppDatabase.getDatabase(context)
        val tokenDao = db.tokenDao()

        val token = runBlocking {
            tokenDao.getToken()
        }

        val request = chain.request().newBuilder()
            .addHeader("Authorization", "${token?.tokenType} ${token?.accessToken}")
            .build()

        return chain.proceed(request)
    }
}