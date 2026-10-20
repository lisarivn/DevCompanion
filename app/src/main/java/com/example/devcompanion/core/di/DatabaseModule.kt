package com.example.devcompanion.core.di

import android.content.Context
import androidx.room.Room
import com.example.devcompanion.data.local.DevCompanionDatabase
import com.example.devcompanion.data.local.dao.FavoriteRepositoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): DevCompanionDatabase {
        return Room.databaseBuilder(
            context,
            DevCompanionDatabase::class.java,
            "devcompanion_database"
        ).build()
    }

    @Provides
    fun provideFavoriteRepositoryDao(
        database: DevCompanionDatabase
    ): FavoriteRepositoryDao {
        return database.favoriteRepositoryDao()
    }
}