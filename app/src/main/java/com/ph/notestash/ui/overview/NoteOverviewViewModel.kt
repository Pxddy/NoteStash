package com.ph.notestash.ui.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.ph.notestash.common.coroutines.dispatcher.DispatcherProvider
import com.ph.notestash.common.time.toLongDateFormat
import com.ph.notestash.data.model.note.Note
import com.ph.notestash.data.model.sort.SortNoteBy
import com.ph.notestash.data.repository.NoteRepository
import com.ph.notestash.ui.overview.list.NoteOverviewListItem
import com.ph.notestash.data.repository.NoteSortingPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class NoteOverviewViewModel @Inject constructor(
    dispatcherProvider: DispatcherProvider,
    noteSortingPreferencesRepository: NoteSortingPreferencesRepository,
    private val noteRepository: NoteRepository
) : ViewModel() {

    private val eventChannel = Channel<NoteOverviewEvent>(Channel.BUFFERED)
    val events = eventChannel.receiveAsFlow()

    val pagingData = noteSortingPreferencesRepository.noteSortingPreferences
        .flatMapLatest { sortPrefs ->
            noteRepository.allNotes(sortedBy = sortPrefs.sortedBy, sortOrder = sortPrefs.sortOrder)
                .map { data -> data.map { it.toNoteOverviewListItem(sortPrefs.sortedBy) } }
        }
        .cachedIn(viewModelScope + dispatcherProvider.Default)

    fun navigateToAddNote() = viewModelScope.launch {
        Timber.d("navigateToAddNote()")
        eventChannel.send(NoteOverviewEvent.NavigateToNoteEdit())
    }

    fun restoreNote(note: Note) = viewModelScope.launch {
        Timber.d("restoreNote(note=%s)", note)
        noteRepository.insertNote(note)
            .onSuccess { Timber.d("Successfully restored note") }
            .onFailure {
                Timber.e(it, "Failed to restore note")
                eventChannel.send(NoteOverviewEvent.RestoreNote(note = note, retry = true))
            }
    }

    fun showSortingDialog() = viewModelScope.launch {
        Timber.d("showSortingDialog()")
        eventChannel.send(NoteOverviewEvent.ShowSortingDialog)
    }

    private fun navigateToEditNote(id: String) = viewModelScope.launch {
        Timber.d("openEditNote(id=%s)", id)
        eventChannel.send(NoteOverviewEvent.NavigateToNoteEdit(id = id))
    }

    private fun deleteNote(id: String, pos: Int) = viewModelScope.launch {
        Timber.d("deleteNote(id=%s, pos=%d)", id, pos)
        noteRepository.deleteNote(id)
            .onFailure {
                Timber.e(it, "Failed to delete note")
                eventChannel.send(NoteOverviewEvent.DeletionFailure(pos))
            }
            .onSuccess { note ->
                Timber.d("Successfully deleted note=%s", note)
                eventChannel.send(NoteOverviewEvent.RestoreNote(note))
            }
    }

    private fun Note.toNoteOverviewListItem(sortedNoteBy: SortNoteBy): NoteOverviewListItem {
        val date = if (sortedNoteBy == SortNoteBy.ModifiedAt) modifiedAt else createdAt
        return NoteOverviewListItem(
            id = id,
            title = title,
            content = content,
            date = date.toLongDateFormat(),
            onClick = this@NoteOverviewViewModel::navigateToEditNote,
            onSwiped = this@NoteOverviewViewModel::deleteNote
        )
    }
}