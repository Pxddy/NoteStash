package com.ph.notestash.note.core.repository

import com.ph.notestash.common.coroutines.dispatcher.DispatcherProvider
import com.ph.notestash.common.coroutines.scope.AppScope
import com.ph.notestash.common.result.checkCancellation
import com.ph.notestash.common.time.TimeProvider
import com.ph.notestash.note.core.model.Note
import com.ph.notestash.note.core.model.UpdateNoteAction
import com.ph.notestash.note.core.model.toMutableNote
import com.ph.notestash.storage.note.NoteDao
import com.ph.notestash.storage.note.toNoteEntity
import dagger.Lazy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepository @Inject constructor(
    @AppScope private val appScope: CoroutineScope,
    private val dispatcherProvider: DispatcherProvider,
    private val noteDaoLazy: Lazy<NoteDao>,
    private val timeProvider: TimeProvider
) {

    // Makes it possible to instantiate the database and dao in a background thread
    private val noteDao get() = noteDaoLazy.get()

    fun allNotes(sortedBy: SortedBy, sortOrder: SortOrder) = flow {
        Timber.d("allNotes(sortedBy=%s, sortOrder=%s)", sortedBy, sortOrder)
        val allNotes = with(noteDao) {
            when (sortOrder) {
                SortOrder.Ascending -> allNotesAscending(sortedBy)
                SortOrder.Descending -> allNotesDescending(sortedBy)
            }
        }
        emitAll(allNotes)
    }

    fun noteForId(id: String): Flow<Note?> = flow {
        Timber.d("noteForId(id=%s)", id)
        emitAll(noteDao.noteForId(id))
    }

    suspend fun insertNote(note: Note) = executeAndAwait {
        Timber.d("insertNote(note=%s)", note)
        noteDao.insertNote(note.toNoteEntity())
    }

    suspend fun deleteNote(id: String): Result<Note> = executeAndAwait {
        Timber.d("deleteNote(id=%s)", id)
        noteDao.deleteNote(id)
    }

    suspend fun updateNote(
        id: String,
        modifiedAt: Instant = timeProvider.now,
        update: UpdateNoteAction
    ) = executeAndAwait {
        Timber.d("updateNote(id=%s)", id)
        noteDao.updateNote(id = id) { note ->
            note.toMutableNote()
                .apply(update)
                .apply { this.modifiedAt = modifiedAt }
                .toNoteEntity()
        }
    }

    private suspend fun <T> executeAndAwait(action: suspend () -> T): Result<T> = runCatching {
        val deferred = appScope.async(dispatcherProvider.IO) { action() }
        deferred.await()
    }.checkCancellation()

    private fun NoteDao.allNotesAscending(sortedBy: SortedBy) = when (sortedBy) {
        SortedBy.Title -> allNotesOrderByTitleAsc()
        SortedBy.CreatedAt -> allNotesOrderByCreatedAtAsc()
        SortedBy.ModifiedAt -> allNotesOrderByModifiedAtAsc()
    }

    private fun NoteDao.allNotesDescending(sortedBy: SortedBy) = when (sortedBy) {
        SortedBy.Title -> allNotesOrderByTitleDesc()
        SortedBy.CreatedAt -> allNotesOrderByCreatedAtDesc()
        SortedBy.ModifiedAt -> allNotesOrderByModifiedAtDesc()
    }

    enum class SortOrder {
        Ascending,
        Descending
    }

    enum class SortedBy {
        Title,
        CreatedAt,
        ModifiedAt
    }
}