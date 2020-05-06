package com.yu.lib.common.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.yu.bundles.pullrefresh.PullRefreshLayout

class PullRefreshClickLayout(context: Context, attrs: AttributeSet?) :
    PullRefreshLayout(context, attrs) {
    private var mIsMove: Boolean = false
    private var mPreY = 0f

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.actionMasked == MotionEvent.ACTION_DOWN) {
            mIsMove = false
            mPreY = ev.y
        } else if (ev?.actionMasked == MotionEvent.ACTION_UP && !mIsMove) {
            mOnClickListener?.onClick(this)
        } else if (ev?.actionMasked == MotionEvent.ACTION_MOVE) {
            if(ev.y != mPreY) {
                mIsMove = true
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private var mOnClickListener: OnClickListener? = null

    override fun setOnClickListener(l: OnClickListener?) {
        mOnClickListener = l
    }
}