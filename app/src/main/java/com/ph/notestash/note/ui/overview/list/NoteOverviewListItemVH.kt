package com.ph.notestash.note.ui.overview.list

import androidx.recyclerview.widget.RecyclerView
import com.ph.notestash.databinding.FragmentNoteOverviewListItemBinding

class NoteOverviewListItemVH(
    private val binding: FragmentNoteOverviewListItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    private var item: NoteOverviewListItem? = null

    fun bind(item: NoteOverviewListItem) = with(binding) {
        this@NoteOverviewListItemVH.item = item

        title.text = item.title
        content.text = item.content

        root.setOnClickListener { item.onclick() }
    }

    fun onSwiped() {
        item?.onSwiped()
    }
}