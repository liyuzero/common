package com.yu.lib.common.ui.adapter.single

import android.util.SparseArray
import android.view.View
import androidx.recyclerview.widget.RecyclerView

@Suppress("UNCHECKED_CAST")
class BaseSingleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    //view 缓存map
    private val mViewMap: SparseArray<View> = SparseArray()

    fun <V : View> getView(id: Int): V? {
        var view: V? = mViewMap.get(id) as V
        if (view == null) {
            view = itemView.findViewById(id)
            mViewMap.put(id, view)
        }
        return view
    }
}