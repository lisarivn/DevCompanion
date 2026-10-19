package com.example.devcompanion.domain.repository

import com.example.devcompanion.domain.model.GitHubRepository
import com.example.devcompanion.domain.model.GitHubRepositoryDetails

interface GitHubRepositoryDataSource {

    suspend fun searchRepositories(
        query: String
    ): List<GitHubRepository>

    suspend fun getRepositoryDetails(
        owner: String,
        repo: String
    ): GitHubRepositoryDetails
}