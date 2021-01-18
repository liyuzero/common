package com.yu.lib.common.widget

import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import com.yu.lib.common.R

class FrameView : FrameLayout {
    private var frameLayout: View? = null
    private var progressBar: ProgressBar? = null
    private var imageView: ImageView? = null
    private var contentView: View? = null
    var mCurColor: Int = 0

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context) : super(context) {
        init(context)
    }

    private fun init(context: Context) {
        val view = LayoutInflater.from(context).inflate(R.layout.common_widget_frame_view, null)
        view.tag = FrameView::class.java
        addView(view)
        frameLayout = findViewById(R.id.frame_layout)
        progressBar = findViewById(R.id.progress)
        imageView = findViewById(R.id.image)

        mCurColor = ContextCompat.getColor(context, android.R.color.black)
        imageView!!.setColorFilter(mCurColor)
        if (Build.VERSION.SDK_INT >= 21) {
            progressBar!!.indeterminateTintList = ColorStateList.valueOf(mCurColor)
        }

        if (contentView == null) {
            for (i in 0 until childCount) {
                val view = getChildAt(i)
                if (view.tag !== FrameView::class.java) {
                    contentView = view
                    contentView!!.visibility = View.GONE
                }
            }
        }
    }

    fun showProgress() {
        frameLayout!!.visibility = View.VISIBLE
        progressBar!!.visibility = View.VISIBLE
        imageView!!.visibility = View.GONE
        if (contentView != null) {
            contentView!!.visibility = View.GONE
        }
    }

    fun showFail() {
        frameLayout!!.visibility = View.VISIBLE
        progressBar!!.visibility = View.GONE
        imageView!!.visibility = View.VISIBLE
        if (contentView != null) {
            contentView!!.visibility = View.GONE
        }
    }

    fun showEmpty() {
        frameLayout!!.visibility = View.VISIBLE
        progressBar!!.visibility = View.GONE
        imageView!!.visibility = View.VISIBLE
        if (contentView != null) {
            contentView!!.visibility = View.GONE
        }
    }

    fun showContent() {
        frameLayout!!.visibility = View.GONE
        if (contentView != null) {
            contentView!!.visibility = View.VISIBLE
        }
    }

    fun addAlwaysVisibleView(view: View, params: FrameLayout.LayoutParams) {
        view.tag = FrameView::class.java
        super.addView(view, params)
    }

}
