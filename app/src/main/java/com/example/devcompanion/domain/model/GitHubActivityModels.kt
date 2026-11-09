package com.example.devcompanion.domain.model

data class GitHubIssue(
    val id: Long,
    val number: Int,
    val title: String,
    val state: String,
    val author: String,
    val createdAt: String,
    val labels: List<String>,
    val body: String? = null,
    val updatedAt: String? = null,
    val closedAt: String? = null,
    val htmlUrl: String? = null
)


data class GitHubPullRequest(
    val id: Long,
    val number: Int,
    val title: String,
    val state: String,
    val author: String,
    val createdAt: String,
    val isMerged: Boolean,
    val headBranch: String,
    val baseBranch: String,
    val body: String? = null,
    val updatedAt: String? = null,
    val mergedAt: String? = null,
    val closedAt: String? = null,
    val htmlUrl: String? = null
)

data class GitHubCommit(
    val sha: String,
    val message: String,
    val author: String,
    val date: String
)