package com.example.proyectotfg_melodix.ViewModel.Request

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.proyectotfg_melodix.Retrofit.MelodixApi.MelodixRepository
import com.example.proyectotfg_melodix.Retrofit.Request.SpotifyRepository

class ContentExplorerViewModelFactory(
    private val repository: SpotifyRepository, private val melodixRepository: MelodixRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContentExplorerViewModel::class.java)) {
            return ContentExplorerViewModel(repository,melodixRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
