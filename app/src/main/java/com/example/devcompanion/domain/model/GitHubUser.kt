package com.example.devcompanion.domain.model

data class GitHubUser(
    val id: Long,
    val login: String,
    val avatarUrl: String,
    val name: String?,
    val bio: String?,
    val company: String?,
    val location: String?,
    val blog: String?,
    val htmlUrl: String,
    val publicRepos: Int,
    val followers: Int,
    val following: Int,
    val createdAt: String
)