package com.ph.notestash.note.ui.overview.list

data class NoteOverviewListItem(
    val title: String,
    val content: String,
    val id: String
) {
    val stableId by lazy { id.hashCode().toLong() }
}
