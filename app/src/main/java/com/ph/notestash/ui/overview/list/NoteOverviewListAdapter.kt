package com.ph.notestash.ui.overview.list

import android.view.LayoutInflater
import android.view.ViewGroup
import com.ph.notestash.common.recyclerview.adapter.GenericListAdapter
import com.ph.notestash.databinding.FragmentNoteOverviewListItemBinding

class NoteOverviewListAdapter : GenericListAdapter<NoteOverviewListItem>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteOverviewListItemVH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = FragmentNoteOverviewListItemBinding.inflate(
            inflater,
            parent,
            false
        )

        return NoteOverviewListItemVH(binding)
    }
}