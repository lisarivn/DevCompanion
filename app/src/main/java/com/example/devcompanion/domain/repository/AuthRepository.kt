package com.example.devcompanion.domain.repository

import com.example.devcompanion.domain.model.GitHubDeviceCode

interface AuthRepository {

    suspend fun requestDeviceCode(): GitHubDeviceCode

    suspend fun awaitAccessToken(
        deviceCode: GitHubDeviceCode
    )

    fun isAuthenticated(): Boolean

    fun logout()
}