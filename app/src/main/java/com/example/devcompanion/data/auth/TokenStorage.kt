package com.example.devcompanion.data.auth

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenStorage @Inject constructor(
    @ApplicationContext context: Context
) {

    companion object {
        private const val KEY_ALIAS = "devcompanion_auth_key"
        private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
        private const val PREFERENCES_NAME = "devcompanion_auth"

        private const val ACCESS_TOKEN_KEY = "access_token"
        private const val REFRESH_TOKEN_KEY = "refresh_token"

        private const val ACCESS_TOKEN_EXPIRES_AT_KEY =
            "access_token_expires_at"

        private const val REFRESH_TOKEN_EXPIRES_AT_KEY =
            "refresh_token_expires_at"

        private const val TRANSFORMATION = "AES/GCM/NoPadding"
    }

    private val preferences = context.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )

    private val keyStore = KeyStore.getInstance(
        KEYSTORE_PROVIDER
    ).apply {
        load(null)
    }

    // Get existing encryption key or generate a new one
    private fun getOrCreateKey(): SecretKey {
        val existingKey = keyStore.getKey(
            KEY_ALIAS,
            null
        ) as? SecretKey

        if (existingKey != null) {
            return existingKey
        }

        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            KEYSTORE_PROVIDER
        )

        val keySpec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or
                    KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(
                KeyProperties.ENCRYPTION_PADDING_NONE
            )
            .setKeySize(256)
            .build()

        keyGenerator.init(keySpec)

        return keyGenerator.generateKey()
    }

    // Encrypt a string using AES-GCM
    private fun encrypt(value: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)

        cipher.init(
            Cipher.ENCRYPT_MODE,
            getOrCreateKey()
        )

        val encrypted = cipher.doFinal(
            value.toByteArray(Charsets.UTF_8)
        )

        val combined = cipher.iv + encrypted

        return Base64.encodeToString(
            combined,
            Base64.NO_WRAP
        )
    }

    // Decrypt a previously encrypted string
    private fun decrypt(encoded: String): String {
        val combined = Base64.decode(
            encoded,
            Base64.NO_WRAP
        )

        require(combined.size > 12) {
            "Invalid encrypted value."
        }

        val iv = combined.copyOfRange(0, 12)

        val encrypted = combined.copyOfRange(
            12,
            combined.size
        )

        val cipher = Cipher.getInstance(TRANSFORMATION)

        cipher.init(
            Cipher.DECRYPT_MODE,
            getOrCreateKey(),
            GCMParameterSpec(128, iv)
        )

        return String(
            cipher.doFinal(encrypted),
            Charsets.UTF_8
        )
    }

    // Save access token, refresh token and expiration timestamps
    @Synchronized
    fun saveTokens(tokens: AuthTokens) {
        require(tokens.accessToken.isNotBlank()) {
            "Access token cannot be blank."
        }

        val encryptedAccessToken = encrypt(tokens.accessToken)

        val encryptedRefreshToken = tokens.refreshToken
            ?.let(::encrypt)

        val editor = preferences.edit()
            .putString(
                ACCESS_TOKEN_KEY,
                encryptedAccessToken
            )

        if (encryptedRefreshToken != null) {
            editor.putString(
                REFRESH_TOKEN_KEY,
                encryptedRefreshToken
            )
        } else {
            editor.remove(REFRESH_TOKEN_KEY)
        }

        if (tokens.accessTokenExpiresAt != null) {
            editor.putLong(
                ACCESS_TOKEN_EXPIRES_AT_KEY,
                tokens.accessTokenExpiresAt
            )
        } else {
            editor.remove(ACCESS_TOKEN_EXPIRES_AT_KEY)
        }

        if (tokens.refreshTokenExpiresAt != null) {
            editor.putLong(
                REFRESH_TOKEN_EXPIRES_AT_KEY,
                tokens.refreshTokenExpiresAt
            )
        } else {
            editor.remove(REFRESH_TOKEN_EXPIRES_AT_KEY)
        }

        check(editor.commit()) {
            "Failed to save authentication tokens."
        }
    }

    // Read and decrypt stored tokens
    @Synchronized
    fun getTokens(): AuthTokens? {
        val encryptedAccessToken = preferences.getString(
            ACCESS_TOKEN_KEY,
            null
        ) ?: return null

        val accessToken = decrypt(encryptedAccessToken)

        val refreshToken = preferences.getString(
            REFRESH_TOKEN_KEY,
            null
        )?.let(::decrypt)

        val accessTokenExpiresAt =
            if (preferences.contains(ACCESS_TOKEN_EXPIRES_AT_KEY)) {
                preferences.getLong(
                    ACCESS_TOKEN_EXPIRES_AT_KEY,
                    0L
                )
            } else {
                null
            }

        val refreshTokenExpiresAt =
            if (preferences.contains(REFRESH_TOKEN_EXPIRES_AT_KEY)) {
                preferences.getLong(
                    REFRESH_TOKEN_EXPIRES_AT_KEY,
                    0L
                )
            } else {
                null
            }

        return AuthTokens(
            accessToken = accessToken,
            refreshToken = refreshToken,
            accessTokenExpiresAt = accessTokenExpiresAt,
            refreshTokenExpiresAt = refreshTokenExpiresAt
        )
    }

    // Remove all locally stored authentication data
    @Synchronized
    fun clearTokens() {
        check(
            preferences.edit()
                .remove(ACCESS_TOKEN_KEY)
                .remove(REFRESH_TOKEN_KEY)
                .remove(ACCESS_TOKEN_EXPIRES_AT_KEY)
                .remove(REFRESH_TOKEN_EXPIRES_AT_KEY)
                .commit()
        ) {
            "Failed to clear authentication tokens."
        }
    }
}