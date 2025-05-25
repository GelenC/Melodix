package com.example.proyectotfg_melodix.ViewModel.Token

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectotfg_melodix.Database.Spotify.TokenEntity
import com.example.proyectotfg_melodix.Database.Spotify.TokenRepository
import com.example.proyectotfg_melodix.Retrofit.Token.SpotifyAuthManager
import com.example.proyectotfg_melodix.Model.Token.TokenResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SpotifyAuthViewModel(private val repository: TokenRepository) : ViewModel() {

    private val _tokenState = MutableStateFlow<TokenResponse?>(null)
    //val tokenState: StateFlow<TokenResponse?> = _tokenState

    private val _isTokenReady = MutableStateFlow(false)
    val isTokenReady: StateFlow<Boolean> = _isTokenReady

    fun checkAndFetchToken(clientId: String, clientSecret: String) {
        viewModelScope.launch {
            val storedToken = repository.getToken()

            if (storedToken == null || repository.isTokenExpired()) {
                val newToken = SpotifyAuthManager.getToken(clientId, clientSecret)
                if (newToken != null) {
                    repository.saveToken(
                        TokenEntity(
                            accessToken = newToken.access_token,
                            expiresIn = newToken.expires_in,
                            timestamp = System.currentTimeMillis() / 1000
                        )
                    )
                    _tokenState.value = newToken
                    _isTokenReady.value = true
                }
            } else {
                _tokenState.value = TokenResponse(
                    access_token = storedToken.accessToken,
                    token_type = storedToken.tokenType,
                    expires_in = storedToken.expiresIn
                )
                _isTokenReady.value = true
            }
        }
    }
}
