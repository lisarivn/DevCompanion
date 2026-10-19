package com.example.devcompanion.feature.repositories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.devcompanion.domain.repository.GitHubRepositoryDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import com.example.devcompanion.domain.model.GitHubIssue
import com.example.devcompanion.domain.model.GitHubPullRequest
import com.example.devcompanion.domain.model.GitHubCommit

data class ActivityUiState<T>(
    val items: List<T> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class RepositoryActivityViewModel @Inject constructor(
    private val repository: GitHubRepositoryDataSource
) : ViewModel() {

    private val _issues =
        MutableStateFlow(ActivityUiState<GitHubIssue>())
    val issues = _issues.asStateFlow()

    private val _pullRequests =
        MutableStateFlow(ActivityUiState<GitHubPullRequest>())
    val pullRequests = _pullRequests.asStateFlow()

    private val _commits =
        MutableStateFlow(ActivityUiState<GitHubCommit>())
    val commits = _commits.asStateFlow()

    private fun errorMessage(exception: Exception): String = when (exception) {
        is HttpException -> when (exception.code()) {
            404 -> "Repository not found."
            403, 429 -> "GitHub rate limit reached."
            else -> "GitHub API error: ${exception.code()}"
        }
        is IOException -> "Network error. Check your internet connection."
        else -> "Unable to load data."
    }

    fun loadIssues(owner: String, repo: String) {
        viewModelScope.launch {
            _issues.value = ActivityUiState(isLoading = true)

            try {
                _issues.value = ActivityUiState(
                    items = repository.getIssues(owner, repo),
                    isLoading = false
                )
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _issues.value = ActivityUiState(
                    isLoading = false,
                    error = errorMessage(exception)
                )
            }
        }
    }

    fun loadPullRequests(owner: String, repo: String) {
        viewModelScope.launch {
            _pullRequests.value = ActivityUiState(isLoading = true)

            try {
                _pullRequests.value = ActivityUiState(
                    items = repository.getPullRequests(owner, repo),
                    isLoading = false
                )
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _pullRequests.value = ActivityUiState(
                    isLoading = false,
                    error = errorMessage(exception)
                )
            }
        }
    }

    fun loadCommits(owner: String, repo: String) {
        viewModelScope.launch {
            _commits.value = ActivityUiState(isLoading = true)

            try {
                _commits.value = ActivityUiState(
                    items = repository.getCommits(owner, repo),
                    isLoading = false
                )
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _commits.value = ActivityUiState(
                    isLoading = false,
                    error = errorMessage(exception)
                )
            }
        }
    }
}