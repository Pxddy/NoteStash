package com.ph.notestash.note.ui.overview.list

import androidx.recyclerview.widget.RecyclerView
import com.ph.notestash.databinding.FragmentNoteOverviewListItemBinding

class NoteOverviewListItemVH(
    private val binding: FragmentNoteOverviewListItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: NoteOverviewListItem) = with(binding) {
        title.text = item.title
        content.text = item.content
    }
}