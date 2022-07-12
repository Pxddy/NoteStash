package com.ph.notestash.common.recyclerview.swipe

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

/**
 * Allows to swipe every [ViewHolder][RecyclerView.ViewHolder] which implements [Swipeable].
 *
 * Swipe directions can be adjusted by swipeDirs.
 */
class SimpleSwipeCallback(
    swipeDirs: Int = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
) : ItemTouchHelper.SimpleCallback(0, swipeDirs) {

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        if (viewHolder is Swipeable) viewHolder.onSwiped(direction)
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int = when {
        viewHolder is Swipeable && viewHolder.canSwipe -> super.getMovementFlags(
            recyclerView,
            viewHolder
        )
        else -> ItemTouchHelper.ACTION_STATE_IDLE
    }
}