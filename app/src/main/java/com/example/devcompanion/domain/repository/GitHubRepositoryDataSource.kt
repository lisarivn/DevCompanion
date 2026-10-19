package com.example.devcompanion.domain.repository

import com.example.devcompanion.domain.model.GitHubRepository

interface GitHubRepositoryDataSource {

    suspend fun searchRepositories(
        query: String
    ): List<GitHubRepository>
}