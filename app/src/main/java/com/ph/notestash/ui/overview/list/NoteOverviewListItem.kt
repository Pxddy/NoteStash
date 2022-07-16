package com.ph.notestash.ui.overview.list

import com.ph.notestash.common.recyclerview.StableIdItem

data class NoteOverviewListItem(
    val id: String,
    val title: String,
    val content: String,
    val date: String,
    private val onClick: (id: String) -> Unit,
    private val onSwiped: (id: String, pos: Int) -> Unit
) : StableIdItem {
    override val stableId by lazy { id.hashCode().toLong() }

    fun onclick() = onClick(id)
    fun onSwiped(pos: Int) = onSwiped(id, pos)
}
