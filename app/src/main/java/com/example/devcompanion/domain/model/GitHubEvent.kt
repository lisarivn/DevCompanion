package com.example.devcompanion.domain.model

data class GitHubEvent(
    val id: String,
    val type: String,
    val repositoryName: String,
    val createdAt: String,
    val description: String
)