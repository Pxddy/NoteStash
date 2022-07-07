package com.ph.notestash.note.ui.overview.list

data class NoteOverviewListItem(
    val title: String,
    val content: String,
    val id: String,
    private val onClick: (id: String) -> Unit,
    private val onSwiped: (id: String) -> Unit
) {
    val stableId by lazy { id.hashCode().toLong() }

    fun onclick() = onClick(id)
    fun onSwiped() = onSwiped(id)
}
