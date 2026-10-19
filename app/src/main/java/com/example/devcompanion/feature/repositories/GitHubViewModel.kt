package com.example.devcompanion.feature.repositories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.devcompanion.domain.model.GitHubRepository
import com.example.devcompanion.domain.repository.GitHubRepositoryDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

data class GitHubUiState(
    val query: String = "",
    val repositories: List<GitHubRepository> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val hasSearched: Boolean = false
)

@HiltViewModel
class GitHubViewModel @Inject constructor(
    private val repository: GitHubRepositoryDataSource
) : ViewModel() {

    private val _uiState = MutableStateFlow(GitHubUiState())
    val uiState = _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun updateQuery(query: String) {
        _uiState.update {
            it.copy(query = query)
        }
    }

    fun searchRepositories() {
        val query = _uiState.value.query.trim()

        if (query.isBlank()) {
            _uiState.update {
                it.copy(
                    error = "Enter a repository name or keyword."
                )
            }
            return
        }

        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    repositories = emptyList(),
                    hasSearched = true
                )
            }

            try {
                val results = repository.searchRepositories(query)

                _uiState.update {
                    it.copy(
                        repositories = results,
                        isLoading = false
                    )
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: HttpException) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = when (exception.code()) {
                            403, 429 -> "GitHub rate limit reached. Try again later."
                            422 -> "Invalid search query."
                            else -> "GitHub API error: ${exception.code()}"
                        }
                    )
                }
            } catch (exception: IOException) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Network error. Check your internet connection."
                    )
                }
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Unable to load repositories."
                    )
                }
            }
        }
    }
}