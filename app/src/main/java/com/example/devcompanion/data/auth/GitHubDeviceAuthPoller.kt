package com.example.devcompanion.data.auth

import android.os.SystemClock
import com.example.devcompanion.data.remote.api.GitHubAuthApiService
import com.example.devcompanion.data.remote.dto.GitHubAccessTokenDto
import com.example.devcompanion.domain.model.GitHubDeviceCode
import kotlinx.coroutines.delay
import javax.inject.Inject
import java.io.IOException

class GitHubDeviceAuthPoller @Inject constructor(
    private val authApi: GitHubAuthApiService
) {

    suspend fun awaitAccessToken(
        clientId: String,
        deviceCode: GitHubDeviceCode
    ): GitHubAccessTokenDto {

        var intervalSeconds = deviceCode.interval.coerceAtLeast(5)

        while (true) {
            val remainingMillis =
                deviceCode.expiresAtElapsedRealtime -
                        SystemClock.elapsedRealtime()

            if (remainingMillis <= 0L) {
                throw IllegalStateException(
                    "GitHub authorization code has expired."
                )
            }

            val delayMillis = minOf(
                intervalSeconds * 1000L,
                remainingMillis
            )

            delay(delayMillis)

            if (SystemClock.elapsedRealtime() >=
                deviceCode.expiresAtElapsedRealtime
            ) {
                throw IllegalStateException(
                    "GitHub authorization code has expired."
                )
            }

            val response = try {
                authApi.requestAccessToken(
                    clientId = clientId,
                    deviceCode = deviceCode.deviceCode
                )
            } catch (e: IOException) {
                // Temporary network error.
                // Retry after the regular polling interval.
                continue
            }

            when (response.error) {
                null -> {
                    if (response.accessToken.isNullOrBlank()) {
                        throw IllegalStateException(
                            "GitHub returned an empty access token."
                        )
                    }

                    return response
                }

                "authorization_pending" -> Unit

                "slow_down" -> {
                    intervalSeconds = maxOf(
                        intervalSeconds + 5,
                        response.interval ?: 0
                    )
                }

                "expired_token", "token_expired" -> {
                    throw IllegalStateException(
                        "GitHub authorization code has expired."
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
                            ?: "GitHub authentication failed: ${response.error}"
                    )
                }
            }
        }
    }
}