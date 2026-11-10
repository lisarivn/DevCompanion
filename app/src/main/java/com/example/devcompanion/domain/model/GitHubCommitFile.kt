package com.example.devcompanion.domain.model

data class GitHubCommitFile(
    val filename: String,
    val status: String,
    val additions: Int,
    val deletions: Int,
    val changes: Int
)