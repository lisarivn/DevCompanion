package com.example.devcompanion.data.auth

import com.example.devcompanion.data.remote.api.GitHubAuthApiService
import com.example.devcompanion.data.remote.dto.GitHubAccessTokenDto
import com.example.devcompanion.data.remote.dto.GitHubDeviceCodeDto
import kotlinx.coroutines.delay
import javax.inject.Inject

class GitHubDeviceAuthPoller @Inject constructor(
    private val authApi: GitHubAuthApiService
) {

    suspend fun awaitAccessToken(
        clientId: String,
        deviceCode: GitHubDeviceCodeDto
    ): GitHubAccessTokenDto {

        var intervalSeconds = deviceCode.interval.coerceAtLeast(5)

        val deadline = System.nanoTime() +
                deviceCode.expiresIn.toLong() * 1_000_000_000L

        while (System.nanoTime() < deadline) {

            delay(intervalSeconds * 1000L)

            if (System.nanoTime() >= deadline) {
                break
            }

            val response = authApi.requestAccessToken(
                clientId = clientId,
                deviceCode = deviceCode.deviceCode
            )

            when (response.error) {

                null -> {
                    if (response.accessToken.isNullOrBlank()) {
                        throw IllegalStateException(
                            "GitHub returned an empty access token."
                        )
                    }

                    return response
                }

                "authorization_pending" -> {
                    // User has not confirmed authorization yet.
                }

                "slow_down" -> {
                    intervalSeconds = maxOf(
                        intervalSeconds + 5,
                        response.interval ?: 0
                    )
                }

                "expired_token", "token_expired" -> {
                    throw IllegalStateException(
                        "Authorization code has expired."
                    )
                }

                "access_denied" -> {
                    throw IllegalStateException(
                        "GitHub authorization was denied."
                    )
                }

                else -> {
                    throw IllegalStateException(
                        response.errorDescription
                            ?: "GitHub authorization failed: ${response.error}"
                    )
                }
            }
        }

        throw IllegalStateException(
            "GitHub authorization timed out."
        )
    }
}