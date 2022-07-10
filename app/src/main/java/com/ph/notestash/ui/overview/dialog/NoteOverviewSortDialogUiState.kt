package com.ph.notestash.ui.overview.dialog

import com.ph.notestash.data.model.sort.SortNoteBy
import com.ph.notestash.data.model.sort.SortOrder

data class NoteOverviewSortDialogUiState(
    val sortNoteBy: SortNoteBy,
    val sortOrder: SortOrder
)
