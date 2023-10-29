package com.ph.notestash.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ph.notestash.data.database.converter.TimeConverter
import com.ph.notestash.data.database.dao.NoteDao
import com.ph.notestash.data.model.note.NoteEntity

@Database(
    entities = [
        NoteEntity::class
    ],
    exportSchema = true,
    version = 1
)
@TypeConverters(TimeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao

    companion object {
        const val DATABASE_NAME = "app-db"
    }
}