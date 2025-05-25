package com.example.proyectotfg_melodix.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.proyectotfg_melodix.Model.Request.Song
import com.example.proyectotfg_melodix.Retrofit.Youtube.YouTubeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun openYouTubeForSong(context: Context, song: Song, repository: YouTubeRepository) {
    CoroutineScope(Dispatchers.IO).launch {
        val videoId = repository.searchFirstVideoId("${song.title} ${song.artist}")
        videoId?.let {
            val videoUrl = "https://www.youtube.com/watch?v=$videoId"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
}
