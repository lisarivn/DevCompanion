package com.example.devcompanion.domain.repository

import com.example.devcompanion.domain.model.GitHubCommit
import com.example.devcompanion.domain.model.GitHubIssue
import com.example.devcompanion.domain.model.GitHubPullRequest
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

    suspend fun getIssues(
        owner: String,
        repo: String
    ): List<GitHubIssue>

    suspend fun getPullRequests(
        owner: String,
        repo: String
    ): List<GitHubPullRequest>

    suspend fun getCommits(
        owner: String,
        repo: String
    ): List<GitHubCommit>
}