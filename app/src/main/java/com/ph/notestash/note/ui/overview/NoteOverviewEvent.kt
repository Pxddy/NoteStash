package com.ph.notestash.note.ui.overview

sealed interface NoteOverviewEvent {
    data class NavigateToNoteEdit(val id: String? = null) : NoteOverviewEvent
}