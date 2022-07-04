package com.ph.notestash.storage.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ph.notestash.storage.database.converter.TimeConverter
import com.ph.notestash.storage.note.NoteDao
import com.ph.notestash.storage.note.NoteEntity

@Database(
    entities = [
        NoteEntity::class
    ],
    exportSchema = true,
    version = 1
)
@TypeConverters(TimeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract val noteDao: NoteDao

    companion object {
        const val DATABASE_NAME = "app-db"
    }
}