package com.ph.core.testing.data.database

import android.content.Context
import androidx.room.Room
import com.ph.notestash.data.database.AppDatabase
import com.ph.notestash.data.database.DatabaseModule
import com.ph.notestash.data.database.dao.NoteDao
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DatabaseModule::class]
)
object DatabaseTestModule {

    @Singleton
    @Provides
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase = Room.inMemoryDatabaseBuilder(
        context,
        AppDatabase::class.java
    )
        .build()

    @Provides
    fun provideNoteDao(appDatabase: AppDatabase): NoteDao = appDatabase.noteDao()
}