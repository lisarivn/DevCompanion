package com.example.devcompanion.data.auth

data class AuthTokens(
    val accessToken: String,
    val refreshToken: String?,
    val accessTokenExpiresAt: Long?,
    val refreshTokenExpiresAt: Long?
)