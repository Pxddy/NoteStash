package com.ph.notestash.note.ui.overview

import com.ph.notestash.note.core.model.Note

sealed interface NoteOverviewEvent {
    data class NavigateToNoteEdit(val id: String? = null) : NoteOverviewEvent
    data class RestoreNote(val note: Note, val retry: Boolean = false) : NoteOverviewEvent
}