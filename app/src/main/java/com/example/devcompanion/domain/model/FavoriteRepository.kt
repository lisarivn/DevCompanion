package com.example.devcompanion.domain.model

data class FavoriteRepository(
    val id: Long,
    val name: String,
    val fullName: String,
    val ownerLogin: String,
    val description: String?,
    val language: String?,
    val starsCount: Int,
    val forksCount: Int,
    val htmlUrl: String,
    val savedAt: Long
)