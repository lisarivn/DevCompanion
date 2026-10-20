package com.example.devcompanion.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.devcompanion.data.local.dao.FavoriteRepositoryDao
import com.example.devcompanion.data.local.entity.FavoriteRepositoryEntity

@Database(
    entities = [FavoriteRepositoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class DevCompanionDatabase : RoomDatabase() {

    abstract fun favoriteRepositoryDao(): FavoriteRepositoryDao
}