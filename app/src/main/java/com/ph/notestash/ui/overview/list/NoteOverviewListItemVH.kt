package com.ph.notestash.ui.overview.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.ph.notestash.common.recyclerview.adapter.BindingViewHolder
import com.ph.notestash.common.recyclerview.swipe.Swipeable
import com.ph.notestash.databinding.FragmentNoteOverviewListItemBinding

class NoteOverviewListItemVH(
    layoutInflater: LayoutInflater,
    parent: ViewGroup
) : BindingViewHolder<NoteOverviewListItem?, FragmentNoteOverviewListItemBinding>(
    binding = FragmentNoteOverviewListItemBinding.inflate(
        layoutInflater,
        parent,
        false
    )
), Swipeable {
    private var item: NoteOverviewListItem? = null

    override fun FragmentNoteOverviewListItemBinding.bind(item: NoteOverviewListItem?) {
        this@NoteOverviewListItemVH.item = item

        loadingView.root.isVisible = item == null
        contentLayout.isVisible = item != null

        title.text = item?.title
        content.text = item?.content
        date.text = item?.date

        root.setOnClickListener { item?.onclick() }
    }

    override val canSwipe: Boolean
        get() = item != null

    override fun onSwiped(direction: Int) {
        item?.onSwiped(bindingAdapterPosition)
    }
}