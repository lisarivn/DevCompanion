package com.example.devcompanion.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.devcompanion.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(
        AuthUiState.Idle
    )

    val uiState: StateFlow<AuthUiState> =
        _uiState.asStateFlow()

    private var authJob: Job? = null

    init {
        checkAuthentication()
    }

    private fun checkAuthentication() {
        viewModelScope.launch {
            try {
                val isAuthenticated = withContext(Dispatchers.IO) {
                    authRepository.isAuthenticated()
                }

                if (_uiState.value == AuthUiState.Idle) {
                    _uiState.value = if (isAuthenticated) {
                        AuthUiState.Authenticated
                    } else {
                        AuthUiState.Idle
                    }
                }

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (_uiState.value == AuthUiState.Idle) {
                    _uiState.value = AuthUiState.Error(
                        e.message ?: "Failed to check authentication."
                    )
                }
            }
        }
    }

    fun startLogin() {
        if (authJob?.isActive == true) {
            return
        }

        authJob = viewModelScope.launch {
            _uiState.value = AuthUiState.Loading

            try {
                val deviceCode = authRepository.requestDeviceCode()

                _uiState.value =
                    AuthUiState.WaitingForAuthorization(
                        userCode = deviceCode.userCode,
                        verificationUri = deviceCode.verificationUri
                    )

                authRepository.awaitAccessToken(deviceCode)

                _uiState.value = AuthUiState.Authenticated

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(
                    e.message ?: "GitHub authentication failed."
                )
            }
        }
    }

    fun cancelLogin() {
        authJob?.cancel()
        authJob = null

        _uiState.value = AuthUiState.Idle
    }

    fun logout() {
        authJob?.cancel()
        authJob = null

        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    authRepository.logout()
                }

                _uiState.value = AuthUiState.Idle

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(
                    e.message ?: "Failed to log out."
                )
            }
        }
    }
}