package com.example.devcompanion.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_repositories")
data class FavoriteRepositoryEntity(
    @PrimaryKey
    val id: Long,
    val name: String,
    val fullName: String,
    val ownerLogin: String,
    val description: String?,
    val language: String?,
    val starsCount: Int,
    val forksCount: Int,
    val htmlUrl: String,
    val savedAt: Long = System.currentTimeMillis()
)