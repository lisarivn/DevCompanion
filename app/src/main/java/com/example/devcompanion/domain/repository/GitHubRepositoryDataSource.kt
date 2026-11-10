package com.example.devcompanion.domain.repository

import com.example.devcompanion.domain.model.GitHubCommit
import com.example.devcompanion.domain.model.GitHubIssue
import com.example.devcompanion.domain.model.GitHubPullRequest
import com.example.devcompanion.domain.model.GitHubRepository
import com.example.devcompanion.domain.model.GitHubRepositoryDetails
import com.example.devcompanion.domain.model.PageResult
import com.example.devcompanion.domain.model.GitHubUser
import com.example.devcompanion.domain.model.GitHubEvent
import com.example.devcompanion.domain.model.GitHubBranch

interface GitHubRepositoryDataSource {

    suspend fun searchRepositories(
        query: String,
        page: Int = 1,
        language: String? = null,
        sort: String? = null,
        order: String = "desc"
    ): PageResult<GitHubRepository>

    suspend fun getRepositoryDetails(
        owner: String,
        repo: String
    ): GitHubRepositoryDetails

    suspend fun getIssues(
        owner: String,
        repo: String,
        page: Int = 1,
        state: String = "all"
    ): PageResult<GitHubIssue>

    suspend fun getIssueDetails(
        owner: String,
        repo: String,
        issueNumber: Int
    ): GitHubIssue

    suspend fun getPullRequests(
        owner: String,
        repo: String,
        page: Int = 1,
        state: String = "all"
    ): PageResult<GitHubPullRequest>

    suspend fun getPullRequestDetails(
        owner: String,
        repo: String,
        pullNumber: Int
    ): GitHubPullRequest

    suspend fun getCommits(
        owner: String,
        repo: String,
        page: Int = 1,
        branch: String? = null
    ): PageResult<GitHubCommit>

    suspend fun getCommitDetails(
        owner: String,
        repo: String,
        sha: String
    ): GitHubCommit


    suspend fun getBranches(
        owner: String,
        repo: String
    ): List<GitHubBranch>

    suspend fun getUserProfile(
        username: String): GitHubUser

    suspend fun getAuthenticatedUser(): GitHubUser

    suspend fun getPublicEvents(username: String): List<GitHubEvent>

    suspend fun getMyRepositories(
        page: Int = 1,
        sort: String = "updated",
        direction: String = "desc"
    ): PageResult<GitHubRepository>

}