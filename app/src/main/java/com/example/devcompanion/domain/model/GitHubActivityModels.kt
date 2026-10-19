package com.example.devcompanion.domain.model

data class GitHubIssue(
    val id: Long,
    val number: Int,
    val title: String,
    val state: String,
    val author: String,
    val createdAt: String,
    val labels: List<String>
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
    val baseBranch: String
)

data class GitHubCommit(
    val sha: String,
    val message: String,
    val author: String,
    val date: String
)