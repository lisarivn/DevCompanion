package com.example.devcompanion.domain.repository

import com.example.devcompanion.domain.model.GitHubDeviceCode
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {

    val isAuthenticatedFlow: StateFlow<Boolean>

    suspend fun requestDeviceCode(): GitHubDeviceCode

    suspend fun awaitAccessToken(
        deviceCode: GitHubDeviceCode
    )

    fun isAuthenticated(): Boolean

    fun logout()
}