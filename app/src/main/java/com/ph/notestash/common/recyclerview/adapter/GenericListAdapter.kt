package com.ph.notestash.common.recyclerview.adapter

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.ph.notestash.common.recyclerview.StableIdItem

abstract class GenericListAdapter<ItemT : StableIdItem>(
    sameItems: (ItemT, ItemT) -> Boolean = { old, new -> old.stableId == new.stableId },
    sameContents: (ItemT, ItemT) -> Boolean = { old, new -> old == new }
) : ListAdapter<ItemT, BindingViewHolder<ItemT, *>>(
    object : DiffUtil.ItemCallback<ItemT>() {
        override fun areItemsTheSame(
            oldItem: ItemT,
            newItem: ItemT
        ): Boolean = sameItems(oldItem, newItem)

        override fun areContentsTheSame(
            oldItem: ItemT,
            newItem: ItemT
        ): Boolean = sameContents(oldItem, newItem)
    }
) {

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long = currentList[position].stableId

    final override fun setHasStableIds(hasStableIds: Boolean) {
        super.setHasStableIds(hasStableIds)
    }

    override fun onBindViewHolder(holder: BindingViewHolder<ItemT, *>, position: Int) {
        val item = currentList[position]
        holder.bind(item = item)
    }
}