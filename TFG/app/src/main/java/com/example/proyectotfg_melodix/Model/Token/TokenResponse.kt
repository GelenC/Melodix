package com.example.proyectotfg_melodix.Model.Token

data class TokenResponse(
    val access_token: String,
    val token_type: String,
    val expires_in: Long
)

