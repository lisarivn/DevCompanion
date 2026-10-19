package com.example.devcompanion.domain.model

data class GitHubRepositoryDetails(
    val id: Long,
    val fullName: String,
    val name: String,
    val description: String?,
    val language: String?,
    val htmlUrl: String,
    val starsCount: Int,
    val forksCount: Int,
    val openIssuesCount: Int,
    val defaultBranch: String,
    val ownerLogin: String,
    val ownerAvatarUrl: String
)