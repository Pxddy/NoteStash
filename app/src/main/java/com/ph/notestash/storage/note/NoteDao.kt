package com.ph.notestash.storage.note

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query("SELECT * FROM notes")
    fun allNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun noteForId(id: String): NoteEntity?

    @Insert
    suspend fun insertNote(noteEntity: NoteEntity)

    @Delete
    suspend fun deleteNote(noteEntity: NoteEntity)

    @Update
    suspend fun updateNote(noteEntity: NoteEntity)

    @Transaction
    // Use database transaction to synchronize the update
    suspend fun updateNote(id: String, update: (note: NoteEntity) -> NoteEntity) {
        val noteToUpdate = requireNotNull(noteForId(id)) { "Found no note for id=${id}" }
        val updated = update(noteToUpdate).also {
            if (it.id != id) throw UnsupportedOperationException("Not allowed to change the id")
        }
        updateNote(updated)
    }
}