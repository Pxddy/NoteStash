package com.ph.notestash.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ph.notestash.common.coroutines.dispatcher.DispatcherProvider
import com.ph.notestash.common.coroutines.scope.AppScope
import com.ph.notestash.common.result.checkCancellation
import com.ph.notestash.common.time.TimeProvider
import com.ph.notestash.data.database.dao.NoteDao
import com.ph.notestash.data.model.note.Note
import com.ph.notestash.data.model.note.UpdateNoteAction
import com.ph.notestash.data.model.note.toMutableNote
import com.ph.notestash.data.model.note.toNoteEntity
import com.ph.notestash.data.model.sort.SortNoteBy
import com.ph.notestash.data.model.sort.SortOrder
import dagger.Lazy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import timber.log.Timber
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

    fun allNotes(sortedBy: SortNoteBy, sortOrder: SortOrder): Flow<PagingData<out Note>> = flow {
        Timber.d("allNotes(sortedBy=%s, sortOrder=%s)", sortedBy, sortOrder)
        val pager = Pager(config = (PagingConfig(pageSize = 50, enablePlaceholders = true)))
        { noteDao.allNotes(sortedBy, sortOrder) }

        emitAll(pager.flow)
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
        update: UpdateNoteAction
    ) = executeAndAwait {
        Timber.d("updateNote(id=%s)", id)
        noteDao.updateNote(id = id) { note ->
            note.toMutableNote()
                .apply { this.modifiedAt = timeProvider.now }
                .apply(update)
                .toNoteEntity()
        }
    }

    private suspend fun <T> executeAndAwait(action: suspend () -> T): Result<T> = runCatching {
        val deferred = appScope.async(dispatcherProvider.IO) { action() }
        deferred.await()
    }.checkCancellation()


    private fun NoteDao.allNotes(sortedBy: SortNoteBy, sortOrder: SortOrder) = when (sortOrder) {
        SortOrder.Ascending -> allNotesAscending(sortedBy)
        SortOrder.Descending -> allNotesDescending(sortedBy)
    }

    private fun NoteDao.allNotesAscending(sortedBy: SortNoteBy) = when (sortedBy) {
        SortNoteBy.Title -> allNotesOrderByTitleAsc()
        SortNoteBy.CreatedAt -> allNotesOrderByCreatedAtAsc()
        SortNoteBy.ModifiedAt -> allNotesOrderByModifiedAtAsc()
    }

    private fun NoteDao.allNotesDescending(sortedBy: SortNoteBy) = when (sortedBy) {
        SortNoteBy.Title -> allNotesOrderByTitleDesc()
        SortNoteBy.CreatedAt -> allNotesOrderByCreatedAtDesc()
        SortNoteBy.ModifiedAt -> allNotesOrderByModifiedAtDesc()
    }
}