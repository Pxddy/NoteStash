package com.ph.notestash.ui.overview

import com.ph.notestash.data.model.note.Note

sealed interface NoteOverviewEvent {
    data class NavigateToNoteEdit(val id: String? = null) : NoteOverviewEvent
    data class RestoreNote(val note: Note, val retry: Boolean = false) : NoteOverviewEvent
    data class DeletionFailure(val pos: Int) : NoteOverviewEvent
    data object ShowSortingDialog : NoteOverviewEvent
}