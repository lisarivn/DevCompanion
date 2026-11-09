package com.example.devcompanion.feature.pullrequests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.devcompanion.domain.model.GitHubPullRequest
import com.example.devcompanion.domain.repository.GitHubRepositoryDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PullRequestDetailsUiState(
    val pullRequest: GitHubPullRequest? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class PullRequestDetailsViewModel @Inject constructor(
    private val repository: GitHubRepositoryDataSource
) : ViewModel() {

    private val _uiState = MutableStateFlow(PullRequestDetailsUiState())
    val uiState = _uiState.asStateFlow()

    private var loadJob: Job? = null

    fun loadPullRequest(
        owner: String,
        repo: String,
        pullNumber: Int
    ) {
        loadJob?.cancel()

        _uiState.value = PullRequestDetailsUiState(isLoading = true)

        loadJob = viewModelScope.launch {
            try {
                val pullRequest = repository.getPullRequestDetails(
                    owner = owner,
                    repo = repo,
                    pullNumber = pullNumber
                )

                _uiState.value = PullRequestDetailsUiState(
                    pullRequest = pullRequest
                )
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.value = PullRequestDetailsUiState(
                    error = exception.message
                        ?: "Unable to load pull request."
                )
            }
        }
    }
}
