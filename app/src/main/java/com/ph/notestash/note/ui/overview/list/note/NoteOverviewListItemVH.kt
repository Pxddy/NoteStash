package com.ph.notestash.note.ui.overview.list.note

import com.ph.notestash.common.recyclerview.adapter.BindingViewHolder
import com.ph.notestash.common.recyclerview.swipe.Swipeable
import com.ph.notestash.databinding.FragmentNoteOverviewListItemBinding

class NoteOverviewListItemVH(
    binding: FragmentNoteOverviewListItemBinding
) : BindingViewHolder<NoteOverviewListItem, FragmentNoteOverviewListItemBinding>(binding),
    Swipeable {
    private var item: NoteOverviewListItem? = null

    override fun FragmentNoteOverviewListItemBinding.bind(item: NoteOverviewListItem) {
        this@NoteOverviewListItemVH.item = item

        title.text = item.title
        content.text = item.content

        root.setOnClickListener { item.onclick() }
    }

    override fun onSwiped(direction: Int) {
        item?.onSwiped()
    }
}