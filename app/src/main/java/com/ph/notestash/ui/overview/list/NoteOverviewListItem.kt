package com.ph.notestash.ui.overview.list

import com.ph.notestash.common.recyclerview.StableIdItem

data class NoteOverviewListItem(
    val title: String,
    val content: String,
    val id: String,
    private val onClick: (id: String) -> Unit,
    private val onSwiped: (id: String) -> Unit
) : StableIdItem {
    override val stableId by lazy { id.hashCode().toLong() }

    fun onclick() = onClick(id)
    fun onSwiped() = onSwiped(id)
}
