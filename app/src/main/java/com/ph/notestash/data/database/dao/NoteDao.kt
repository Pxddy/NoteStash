package com.ph.notestash.data.database.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.ph.notestash.data.model.note.NoteEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

@Dao
interface NoteDao {

    @Query("SELECT * FROM notes ORDER BY title COLLATE NOCASE ASC")
    fun allNotesOrderByTitleAsc(): PagingSource<Int, NoteEntity>

    @Query("SELECT * FROM notes ORDER BY title COLLATE NOCASE DESC")
    fun allNotesOrderByTitleDesc(): PagingSource<Int, NoteEntity>

    @Query("SELECT * FROM notes ORDER BY datetime(created_at) ASC")
    fun allNotesOrderByCreatedAtAsc(): PagingSource<Int, NoteEntity>

    @Query("SELECT * FROM notes ORDER BY datetime(created_at) DESC")
    fun allNotesOrderByCreatedAtDesc(): PagingSource<Int, NoteEntity>

    @Query("SELECT * FROM notes ORDER BY datetime(modified_at) ASC")
    fun allNotesOrderByModifiedAtAsc(): PagingSource<Int, NoteEntity>

    @Query("SELECT * FROM notes ORDER BY datetime(modified_at) DESC")
    fun allNotesOrderByModifiedAtDesc(): PagingSource<Int, NoteEntity>

    @Query("SELECT * FROM notes WHERE id = :id")
    fun noteForId(id: String): Flow<NoteEntity?>

    @Insert
    suspend fun insertNote(noteEntity: NoteEntity)

    @Delete
    suspend fun deleteNote(noteEntity: NoteEntity)

    @Transaction
    suspend fun deleteNote(id: String): NoteEntity {
        val noteToDelete = requireNote(id)
        deleteNote(noteToDelete)
        return noteToDelete
    }

    @Update
    suspend fun updateNote(noteEntity: NoteEntity)

    @Transaction
    // Use database transaction to synchronize the update
    suspend fun updateNote(id: String, update: suspend (note: NoteEntity) -> NoteEntity) {
        val noteToUpdate = requireNote(id)
        val updated = update(noteToUpdate).also {
            if (it.id != id) throw UnsupportedOperationException("Not allowed to change the id")
        }
        updateNote(updated)
    }
}

private suspend fun NoteDao.requireNote(id: String): NoteEntity {
    return requireNotNull(noteForId(id).firstOrNull()) { "Found no note for id=${id}" }
}