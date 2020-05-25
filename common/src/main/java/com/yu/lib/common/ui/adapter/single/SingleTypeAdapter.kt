package com.yu.lib.common.ui.adapter.single

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.util.HashSet

abstract class SingleTypeAdapter<D>(
    private val mLayoutRes: Int,
    private val mData: MutableList<D>
) : RecyclerView.Adapter<BaseSingleViewHolder>() {
    private var mOnItemClickListener: OnItemClickListener? = null
    private var mOnItemLongClickListener: OnItemLongClickListener? = null
    private var mOnChildItemClickListener: OnChildItemClickListener? = null
    private var mChildIdSet: MutableSet<Int>? = null

    val data: MutableList<D>?
        get() = mData

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseSingleViewHolder {
        val holder = BaseSingleViewHolder(
            LayoutInflater.from(parent.context).inflate(
                mLayoutRes,
                parent,
                false
            )
        )

        holder.itemView.setOnClickListener { v ->
            mOnItemClickListener?.onItemClick(v, holder.layoutPosition)
        }
        holder.itemView.setOnLongClickListener { v ->
            if (mOnItemLongClickListener != null) {
                val b = mOnItemLongClickListener!!.onItemLongClick(v, holder.layoutPosition)
                b
            } else {
                false
            }
        }
        if (mChildIdSet != null) {
            for (id in mChildIdSet!!) {
                holder.itemView.findViewById<View>(id).setOnClickListener { v ->
                    if (mOnChildItemClickListener != null) {
                        mOnChildItemClickListener!!.onChildItemClick(v, holder.layoutPosition)
                    }
                }
            }
        }

        return holder
    }

    override fun onBindViewHolder(holderSingle: BaseSingleViewHolder, position: Int) {
        onBindData(holderSingle, mData[position], position)
    }

    protected abstract fun onBindData(holderSingle: BaseSingleViewHolder, data: D, position: Int)

    override fun getItemCount(): Int {
        return mData.size ?: 0
    }

    fun notifyData(data: MutableList<D>) {
        mData.clear()
        mData.addAll(data)
        notifyDataSetChanged()
    }

    fun setNewData(data: MutableList<D>) {
        mData.clear()
        mData.addAll(data)
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        mOnItemClickListener = onItemClickListener
    }

    fun setOnItemLongClickListener(onItemLongClickListener: OnItemLongClickListener) {
        mOnItemLongClickListener = onItemLongClickListener
    }

    fun setOnChildItemClickListener(
        childId: Int,
        onChildItemClickListener: OnChildItemClickListener
    ) {
        mOnChildItemClickListener = onChildItemClickListener
        if (mChildIdSet == null) {
            mChildIdSet = HashSet()
        }
        mChildIdSet!!.add(childId)
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(view: View, position: Int): Boolean
    }

    interface OnChildItemClickListener {
        fun onChildItemClick(view: View, position: Int)
    }

    fun getItem(position: Int): D {
        return mData[position]
    }
}