package com.example.devcompanion.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GitHubAccessTokenDto(
    @SerializedName("access_token")
    val accessToken: String? = null,

    @SerializedName("token_type")
    val tokenType: String? = null,

    val scope: String? = null,

    @SerializedName("expires_in")
    val expiresIn: Long? = null,

    @SerializedName("refresh_token")
    val refreshToken: String? = null,

    @SerializedName("refresh_token_expires_in")
    val refreshTokenExpiresIn: Long? = null,

    val error: String? = null,

    @SerializedName("error_description")
    val errorDescription: String? = null,

    val interval: Int? = null
)