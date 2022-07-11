package com.ph.notestash.ui.overview.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.ph.notestash.common.coroutines.dispatcher.DispatcherProvider
import com.ph.notestash.common.recyclerview.adapter.GenericListAdapter
import com.ph.notestash.databinding.FragmentNoteOverviewListItemBinding

class NoteOverviewListAdapter(dispatcherProvider: DispatcherProvider) :
    PagingDataAdapter<NoteOverviewListItem, NoteOverviewListItemVH>(
        diffCallback = NoteOverviewListItemItemCallback(),
        mainDispatcher = dispatcherProvider.Main,
        workerDispatcher = dispatcherProvider.Default
    ) {

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
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }
}

private class NoteOverviewListItemItemCallback : DiffUtil.ItemCallback<NoteOverviewListItem>() {
    override fun areItemsTheSame(
        oldItem: NoteOverviewListItem,
        newItem: NoteOverviewListItem
    ): Boolean = oldItem.stableId == newItem.stableId

    override fun areContentsTheSame(
        oldItem: NoteOverviewListItem,
        newItem: NoteOverviewListItem
    ): Boolean = oldItem == newItem
}