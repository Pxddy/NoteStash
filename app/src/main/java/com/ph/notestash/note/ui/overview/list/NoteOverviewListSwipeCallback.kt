package com.ph.notestash.note.ui.overview.list

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class NoteOverviewListSwipeCallback : ItemTouchHelper.SimpleCallback(
    0,
    ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT
) {
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        if (viewHolder is NoteOverviewListItemVH) viewHolder.onSwiped()
    }
}