package com.example.devcompanion.feature.issues

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.devcompanion.domain.model.GitHubIssue
import com.example.devcompanion.domain.repository.GitHubRepositoryDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class IssueDetailsUiState(
    val issue: GitHubIssue? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class IssueDetailsViewModel @Inject constructor(
    private val repository: GitHubRepositoryDataSource
) : ViewModel() {

    private val _uiState = MutableStateFlow(IssueDetailsUiState())
    val uiState = _uiState.asStateFlow()

    private var loadJob: Job? = null

    fun loadIssue(owner: String, repo: String, issueNumber: Int) {
        loadJob?.cancel()

        _uiState.value = IssueDetailsUiState(isLoading = true)

        loadJob = viewModelScope.launch {
            try {
                val issue = repository.getIssueDetails(
                    owner = owner,
                    repo = repo,
                    issueNumber = issueNumber
                )

                _uiState.value = IssueDetailsUiState(issue = issue)
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.value = IssueDetailsUiState(
                    error = exception.message ?: "Unable to load issue."
                )
            }
        }
    }
}
