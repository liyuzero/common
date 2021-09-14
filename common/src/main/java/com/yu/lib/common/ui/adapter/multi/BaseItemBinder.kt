package com.yu.lib.common.ui.adapter.multi

import android.content.Context
import android.view.View

abstract class BaseItemBinder<T> {
    private var mViewTypeStr: String? = null
    var viewType = 0

    open fun getViewTypeStr(): String? {
        return mViewTypeStr
    }

    open fun setViewTypeStr(viewTypeStr: String?) {
        mViewTypeStr = viewTypeStr
    }

    abstract fun getLayoutRes(): Int

    open fun getView(context: Context?, viewType: Int): View? {
        return null
    }

    abstract fun onCreateViewHolder(holder: MultiRecyclerAdapter<T>.BaseViewHolder)
    abstract fun onBindViewHolder(holder: MultiRecyclerAdapter<T>.BaseViewHolder, position: Int, data: T)

    open fun onViewRecycled(holder: MultiRecyclerAdapter<T>.BaseViewHolder) {}

    open fun onViewAttachedToWindow(holder: MultiRecyclerAdapter<T>.BaseViewHolder) {}

    open fun onViewDetachedFromWindow(holder: MultiRecyclerAdapter<T>.BaseViewHolder) {}
}