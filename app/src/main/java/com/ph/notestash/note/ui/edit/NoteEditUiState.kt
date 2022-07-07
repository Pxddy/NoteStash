package com.ph.notestash.note.ui.edit

sealed interface NoteEditUiState {
    object Loading : NoteEditUiState
    object Failure : NoteEditUiState
    data class Success(
        val title: String,
        val content: String
    ) : NoteEditUiState
}