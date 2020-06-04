package com.yu.lib.common.ui.adapter

import androidx.recyclerview.widget.DiffUtil

class DiffCallback<T>(private val oldList: List<T>, private val newList: List<T>) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = oldList[newItemPosition]
        if(oldItem == newItem && oldItem == null) {
            return true
        }
        return if(oldItem == null || newItem == null) {
            false
        } else {
            oldItem.hashCode() == newItem.hashCode()
        }
    }

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = oldList[newItemPosition]
        return oldItem?.equals(newItem) ?: false
    }
}