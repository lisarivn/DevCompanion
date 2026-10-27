package com.example.devcompanion.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GitHubUserDto(
    val login: String,

    val id: Long? = null,

    @SerializedName("avatar_url")
    val avatarUrl: String? = null,

    val name: String? = null,
    val bio: String? = null,
    val company: String? = null,
    val location: String? = null,
    val blog: String? = null,

    @SerializedName("html_url")
    val htmlUrl: String? = null,

    @SerializedName("public_repos")
    val publicRepos: Int? = null,

    val followers: Int? = null,
    val following: Int? = null,

    @SerializedName("created_at")
    val createdAt: String? = null
)