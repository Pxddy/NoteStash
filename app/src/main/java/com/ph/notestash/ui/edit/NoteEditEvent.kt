package com.ph.notestash.ui.edit

sealed interface NoteEditEvent {
    data object NavigateBack : NoteEditEvent
    data object ShowDeletionConfirmationDialog : NoteEditEvent
}