package com.example.devcompanion.feature.commits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.devcompanion.domain.model.GitHubCommit
import com.example.devcompanion.domain.repository.GitHubRepositoryDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

data class CommitDetailsUiState(
    val commit: GitHubCommit? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val commitKey: String? = null
)

@HiltViewModel
class CommitDetailsViewModel @Inject constructor(
    private val repository: GitHubRepositoryDataSource
) : ViewModel() {

    private val _uiState = MutableStateFlow(CommitDetailsUiState())
    val uiState = _uiState.asStateFlow()

    private var loadJob: Job? = null

    fun loadCommitDetails(
        owner: String,
        repo: String,
        sha: String,
        forceRefresh: Boolean = false
    ) {
        val key = "$owner/$repo/$sha"

        val current = _uiState.value

        if (!forceRefresh &&
            current.commitKey == key &&
            (current.isLoading || current.commit != null)
        ) {
            return
        }

        loadJob?.cancel()

        _uiState.value = CommitDetailsUiState(
            isLoading = true,
            commitKey = key
        )

        loadJob = viewModelScope.launch {
            try {
                val commit = repository.getCommitDetails(
                    owner = owner,
                    repo = repo,
                    sha = sha
                )

                if (_uiState.value.commitKey != key) {
                    return@launch
                }

                _uiState.value = CommitDetailsUiState(
                    commit = commit,
                    commitKey = key
                )
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                if (_uiState.value.commitKey == key) {
                    _uiState.value = CommitDetailsUiState(
                        error = errorMessage(exception),
                        commitKey = key
                    )
                }
            }
        }
    }

    private fun errorMessage(exception: Exception): String {
        return when (exception) {
            is HttpException -> when (exception.code()) {
                401 -> "Authentication required."
                403, 429 -> "GitHub API rate limit reached."
                404 -> "Commit not found."
                422 -> "Invalid commit SHA."
                else -> "GitHub API error: ${exception.code()}"
            }

            is IOException ->
                "Network error. Check your internet connection."

            else -> "Unable to load commit details."
        }
    }
}
