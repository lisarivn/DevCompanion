package com.example.devcompanion.data.repository

import com.example.devcompanion.data.mapper.toDomain
import com.example.devcompanion.data.remote.api.GitHubApiService
import com.example.devcompanion.domain.model.GitHubRepository
import com.example.devcompanion.domain.repository.GitHubRepositoryDataSource
import javax.inject.Inject

class GitHubRepositoryImpl @Inject constructor(
    private val apiService: GitHubApiService
) : GitHubRepositoryDataSource {

    override suspend fun searchRepositories(
        query: String
    ): List<GitHubRepository> {
        return apiService.searchRepositories(query)
            .items
            .map { it.toDomain() }
    }
}