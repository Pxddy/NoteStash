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

    private val noteDao get() = noteDaoLazy.get()

    //TODO: Add Sorting
    fun allNotes() = noteDao.allNotes()

    fun noteForId(id: String): Flow<Note?> {
        Timber.d("noteForId(id=%s)", id)
        return noteDao.noteForId(id)
    }

    suspend fun insertNote(note: Note) = executeAndAwait {
        Timber.d("insertNote(note=%s)", note)
        noteDao.insertNote(note.toNoteEntity())
    }

    suspend fun deleteNote(id: String): Result<Note> = executeAndAwait {
        Timber.d("deleteNote(id=%s) - Thread=%s", id, Thread.currentThread().name)
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
}