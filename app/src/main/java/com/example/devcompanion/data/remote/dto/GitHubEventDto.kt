package com.example.devcompanion.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GitHubEventDto(
    val id: String,
    val type: String,
    val repo: GitHubEventRepoDto,
    @SerializedName("created_at")
    val createdAt: String
)

data class GitHubEventRepoDto(
    val name: String
)