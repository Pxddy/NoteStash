package com.ph.notestash.note.ui.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ph.notestash.common.coroutines.dispatcher.DispatcherProvider
import com.ph.notestash.note.core.model.DefaultNote
import com.ph.notestash.note.core.model.Note
import com.ph.notestash.note.core.repository.NoteRepository
import com.ph.notestash.note.ui.overview.list.NoteOverviewListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import timber.log.Timber
import java.time.Instant
import java.util.*
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class NoteOverviewViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    private val eventChannel = Channel<NoteOverviewEvent>(Channel.BUFFERED)
    val events = eventChannel.receiveAsFlow()

    val uiState: StateFlow<NoteOverviewUiState> = noteRepository.allNotes()
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

    fun navigateToEditNote(id: String) = viewModelScope.launch {
        Timber.d("openEditNote(id=%s)", id)
        eventChannel.send(NoteOverviewEvent.NavigateToNoteEdit(id = id))
    }

    private fun List<Note>.toNoteOverviewUiState(): NoteOverviewUiState {
        return NoteOverviewUiState.Success(
            notes = map { it.toNoteOverviewListItem() }
        )
    }

    private fun Note.toNoteOverviewListItem() = NoteOverviewListItem(
        title = title,
        content = content,
        id = id
    )
}