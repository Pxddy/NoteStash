package com.ph.notestash.note.ui.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ph.notestash.common.coroutines.dispatcher.DispatcherProvider
import com.ph.notestash.note.core.model.Note
import com.ph.notestash.note.core.repository.NoteRepository
import com.ph.notestash.note.ui.overview.list.note.NoteOverviewListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class NoteOverviewViewModel @Inject constructor(
    dispatcherProvider: DispatcherProvider,
    private val noteRepository: NoteRepository,
) : ViewModel() {

    private val eventChannel = Channel<NoteOverviewEvent>(Channel.BUFFERED)
    val events = eventChannel.receiveAsFlow()

    val uiState: StateFlow<NoteOverviewUiState> = noteRepository.allNotes(
        sortedBy = NoteRepository.SortedBy.CreatedAt,
        sortOrder = NoteRepository.SortOrder.Descending
    )
        .map { it.toNoteOverviewUiState() }
        .catch {
            Timber.e(it, "Failed to load notes")
            emit(NoteOverviewUiState.Failure)
        }
        .stateIn(
            scope = viewModelScope + dispatcherProvider.Default,
            started = SharingStarted.WhileSubscribed(5.seconds),
            initialValue = NoteOverviewUiState.Loading
        )

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

    private fun navigateToEditNote(id: String) = viewModelScope.launch {
        Timber.d("openEditNote(id=%s)", id)
        eventChannel.send(NoteOverviewEvent.NavigateToNoteEdit(id = id))
    }

    private fun deleteNote(id: String) = viewModelScope.launch {
        Timber.d("deleteNote(id=%s)", id)
        noteRepository.deleteNote(id)
            .onFailure { Timber.e(it, "Failed to delete note") }
            .onSuccess { note ->
                Timber.d("Successfully deleted note=%s", note)
                eventChannel.send(NoteOverviewEvent.RestoreNote(note))
            }
    }

    private fun List<Note>.toNoteOverviewUiState(): NoteOverviewUiState {
        return NoteOverviewUiState.Success(
            notes = map { it.toNoteOverviewListItem() }
        )
    }

    private fun Note.toNoteOverviewListItem() = NoteOverviewListItem(
        title = title,
        content = content,
        id = id,
        onClick = this@NoteOverviewViewModel::navigateToEditNote,
        onSwiped = this@NoteOverviewViewModel::deleteNote
    )
}