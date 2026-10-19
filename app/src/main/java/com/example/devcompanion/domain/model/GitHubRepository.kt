package com.example.devcompanion.domain.model

data class GitHubRepository(
    val id: Long,
    val fullName: String,
    val description: String?,
    val language: String?,
    val htmlUrl: String,
    val starsCount: Int
)