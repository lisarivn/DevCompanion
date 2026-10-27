package com.example.devcompanion.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.devcompanion.domain.model.GitHubUser
import com.example.devcompanion.domain.repository.GitHubRepositoryDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

sealed interface ProfileUiState {

    data object Idle : ProfileUiState

    data object Loading : ProfileUiState

    data class Success(
        val user: GitHubUser
    ) : ProfileUiState

    data class Error(
        val message: String
    ) : ProfileUiState
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: GitHubRepositoryDataSource
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<ProfileUiState>(ProfileUiState.Idle)

    val uiState: StateFlow<ProfileUiState> =
        _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun loadProfile(username: String) {

        val normalizedUsername = username.trim()

        searchJob?.cancel()

        if (normalizedUsername.isBlank()) {
            _uiState.value = ProfileUiState.Idle
            return
        }

        searchJob = viewModelScope.launch {

            _uiState.value = ProfileUiState.Loading

            try {
                val user = repository.getUserProfile(normalizedUsername)

                _uiState.value = ProfileUiState.Success(user)

            } catch (exception: CancellationException) {
                throw exception

            } catch (exception: HttpException) {

                val message = when (exception.code()) {
                    404 -> "GitHub user not found."
                    403 -> "GitHub API access forbidden or rate limit exceeded."
                    429 -> "Too many requests. Please try again later."
                    else -> "GitHub API error: ${exception.code()}"
                }

                _uiState.value = ProfileUiState.Error(message)

            } catch (exception: IOException) {

                _uiState.value = ProfileUiState.Error(
                    "Network error. Check your internet connection."
                )

            } catch (exception: Exception) {

                _uiState.value = ProfileUiState.Error(
                    "Failed to load GitHub profile."
                )
            }
        }
    }

    fun clearProfile() {
        searchJob?.cancel()
        _uiState.value = ProfileUiState.Idle
    }
}