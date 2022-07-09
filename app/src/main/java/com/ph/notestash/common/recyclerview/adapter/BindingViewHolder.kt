package com.ph.notestash.common.recyclerview.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BindingViewHolder<ItemT, ViewBindingT : ViewBinding>(
    private val binding: ViewBindingT
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: ItemT) = binding.bind(item)

    protected abstract fun ViewBindingT.bind(item: ItemT)

    protected val context: Context get() = itemView.context
}