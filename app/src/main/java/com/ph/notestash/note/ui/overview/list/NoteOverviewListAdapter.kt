package com.ph.notestash.note.ui.overview.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.ph.notestash.databinding.FragmentNoteOverviewListItemBinding

class NoteOverviewListAdapter : ListAdapter<NoteOverviewListItem, NoteOverviewListItemVH>(
    object : DiffUtil.ItemCallback<NoteOverviewListItem>() {
        override fun areItemsTheSame(
            oldItem: NoteOverviewListItem,
            newItem: NoteOverviewListItem
        ): Boolean = oldItem.stableId == newItem.stableId

        override fun areContentsTheSame(
            oldItem: NoteOverviewListItem,
            newItem: NoteOverviewListItem
        ): Boolean = oldItem == newItem
    }
) {

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long = currentList[position].stableId

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteOverviewListItemVH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = FragmentNoteOverviewListItemBinding.inflate(
            inflater,
            parent,
            false
        )

        return NoteOverviewListItemVH(binding)
    }

    override fun onBindViewHolder(holder: NoteOverviewListItemVH, position: Int) {
        val item = currentList[position]
        holder.bind(item)
    }
}