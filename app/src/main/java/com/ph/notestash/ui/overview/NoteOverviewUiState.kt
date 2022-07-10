package com.ph.notestash.ui.overview

import com.ph.notestash.ui.overview.list.NoteOverviewListItem

sealed interface NoteOverviewUiState {
    object Loading : NoteOverviewUiState
    object Failure : NoteOverviewUiState
    data class Success(val notes: List<NoteOverviewListItem>) : NoteOverviewUiState
}