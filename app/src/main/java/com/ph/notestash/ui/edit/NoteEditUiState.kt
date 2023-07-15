package com.ph.notestash.ui.edit

sealed interface NoteEditUiState {
    data object Loading : NoteEditUiState
    data object Failure : NoteEditUiState
    data class Success(
        val title: String,
        val content: String
    ) : NoteEditUiState
}