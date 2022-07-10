package com.ph.notestash.note.ui.overview.dialog

import com.ph.notestash.storage.model.sort.SortNoteBy
import com.ph.notestash.storage.model.sort.SortOrder

data class NoteOverviewSortDialogUiState(
    val sortNoteBy: SortNoteBy,
    val sortOrder: SortOrder
)
