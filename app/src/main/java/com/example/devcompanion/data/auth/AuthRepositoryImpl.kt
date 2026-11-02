package com.example.devcompanion.data.auth

import com.example.devcompanion.data.mapper.toDomain
import com.example.devcompanion.data.remote.api.GitHubAuthApiService
import com.example.devcompanion.domain.model.GitHubDeviceCode
import com.example.devcompanion.domain.repository.AuthRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepositoryImpl @Inject constructor(
    private val authApi: GitHubAuthApiService,
    private val poller: GitHubDeviceAuthPoller,
    private val tokenStorage: TokenStorage
) : AuthRepository {

    companion object {
        private const val CLIENT_ID = "Ov23liOlvrdoA5uGBnjN"
    }

    private val _isAuthenticatedFlow = MutableStateFlow(
        isAuthenticated()
    )

    override val isAuthenticatedFlow: StateFlow<Boolean> =
        _isAuthenticatedFlow.asStateFlow()

    override suspend fun requestDeviceCode(): GitHubDeviceCode {
        return authApi.requestDeviceCode(
            clientId = CLIENT_ID
        ).toDomain()
    }

    override suspend fun awaitAccessToken(
        deviceCode: GitHubDeviceCode
    ) {
        val response = poller.awaitAccessToken(
            clientId = CLIENT_ID,
            deviceCode = deviceCode
        )

        val accessToken = requireNotNull(response.accessToken) {
            "GitHub returned an empty access token."
        }

        val currentTime = System.currentTimeMillis()

        val tokens = AuthTokens(
            accessToken = accessToken,
            refreshToken = response.refreshToken,
            accessTokenExpiresAt = response.expiresIn?.let {
                currentTime + it * 1000L
            },
            refreshTokenExpiresAt = response.refreshTokenExpiresIn?.let {
                currentTime + it * 1000L
            }
        )

        tokenStorage.saveTokens(tokens)
        _isAuthenticatedFlow.value = isAuthenticated()
    }

    override fun isAuthenticated(): Boolean {
        val tokens = tokenStorage.getTokens() ?: return false

        val expiresAt = tokens.accessTokenExpiresAt
            ?: return false

        val currentTime = System.currentTimeMillis()

        return tokens.accessToken.isNotBlank() &&
                currentTime < expiresAt
    }

    override fun logout() {
        tokenStorage.clearTokens()
        _isAuthenticatedFlow.value = false
    }
}