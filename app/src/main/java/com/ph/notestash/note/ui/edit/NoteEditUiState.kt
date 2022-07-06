package com.ph.notestash.note.ui.edit

sealed interface NoteEditUiState {
    object Loading : NoteEditUiState
    object Error : NoteEditUiState
    data class Success(
        val title: String,
        val content: String
    ) : NoteEditUiState
}