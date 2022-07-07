package com.ph.notestash.note.ui.edit

sealed interface NoteEditEvent {
    object NavigateBack : NoteEditEvent
}