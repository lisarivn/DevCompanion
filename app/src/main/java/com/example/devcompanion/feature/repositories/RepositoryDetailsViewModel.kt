package com.example.devcompanion.feature.repositories

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.devcompanion.domain.model.GitHubRepositoryDetails
import com.example.devcompanion.domain.repository.GitHubRepositoryDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

data class RepositoryDetailsUiState(
    val repository: GitHubRepositoryDetails? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class RepositoryDetailsViewModel @Inject constructor(
    private val repositoryDataSource: GitHubRepositoryDataSource,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val owner: String = checkNotNull(savedStateHandle["owner"])
    private val repo: String = checkNotNull(savedStateHandle["repo"])

    private val _uiState = MutableStateFlow(RepositoryDetailsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadRepository()
    }

    fun loadRepository() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, error = null)
            }

            try {
                val details = repositoryDataSource.getRepositoryDetails(
                    owner = owner,
                    repo = repo
                )

                _uiState.update {
                    it.copy(
                        repository = details,
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
                            404 -> "Repository not found."
                            403, 429 -> "GitHub API rate limit reached."
                            else -> "GitHub API error: ${exception.code()}"
                        }
                    )
                }
            } catch (exception: IOException) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Network error. Check your connection."
                    )
                }
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Unable to load repository details."
                    )
                }
            }
        }
    }
}