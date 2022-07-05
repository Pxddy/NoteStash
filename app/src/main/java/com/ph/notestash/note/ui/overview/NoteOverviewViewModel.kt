package com.ph.notestash.note.ui.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ph.notestash.common.coroutines.dispatcher.DispatcherProvider
import com.ph.notestash.note.core.model.Note
import com.ph.notestash.note.core.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.plus
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class NoteOverviewViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
    dispatcherProvider: DispatcherProvider
) : ViewModel() {

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

    private fun List<Note>.toNoteOverviewUiState(): NoteOverviewUiState {
        return NoteOverviewUiState.Success(notes = this)
    }
}