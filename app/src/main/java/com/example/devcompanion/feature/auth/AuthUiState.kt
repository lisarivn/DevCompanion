package com.example.devcompanion.feature.auth

sealed interface AuthUiState {

    data object Idle : AuthUiState

    data object Loading : AuthUiState

    data class WaitingForAuthorization(
        val userCode: String,
        val verificationUri: String
    ) : AuthUiState

    data object Authenticated : AuthUiState

    data class Error(
        val message: String
    ) : AuthUiState
}