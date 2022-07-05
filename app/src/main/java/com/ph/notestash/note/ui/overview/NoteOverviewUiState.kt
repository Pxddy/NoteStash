package com.ph.notestash.note.ui.overview

import com.ph.notestash.note.core.model.Note

sealed interface NoteOverviewUiState{
    object Loading: NoteOverviewUiState
    object Failure: NoteOverviewUiState
    data class Success(val notes: List<Note>): NoteOverviewUiState
}