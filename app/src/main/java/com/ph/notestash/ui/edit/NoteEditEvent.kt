package com.ph.notestash.ui.edit

sealed interface NoteEditEvent {
    object NavigateBack : NoteEditEvent
    object ShowDeletionConfirmationDialog : NoteEditEvent
}