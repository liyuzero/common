package com.yu.lib.common.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.CheckBox
import android.widget.TextView

import java.util.Arrays
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yu.lib.common.CommonManager
import com.yu.lib.common.R
import com.yu.lib.common.ui.adapter.single.BaseSingleViewHolder
import com.yu.lib.common.ui.adapter.single.SingleTypeAdapter

class CheckBoxDialog(context: Context) : Dialog(context, R.style.dialog) {
    private val mBean: Bean
    private var mCurBox: CheckBox? = null

    init {
        mBean = Bean()
    }

    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.common_check_box_dialog)

        (findViewById<View>(R.id.title) as TextView).text = mBean.title
        val sureView = findViewById<TextView>(R.id.sure)
        sureView.setTextColor(CommonManager.getMainColor())
        sureView.setOnClickListener { v -> mBean.mOnItemClickListener!!.onClick(this, mBean.curCheckBox) }
        findViewById<View>(R.id.cancel).setOnClickListener({ v -> dismiss() })

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        val singleTypeAdapter = object : SingleTypeAdapter<String>(R.layout.common_check_box_dialog_custom, Arrays.asList(*mBean.items!!)) {
            @SuppressLint("ClickableViewAccessibility")
            override fun onBindData(holderSingle: BaseSingleViewHolder, data: String, position: Int) {
                (holderSingle.itemView.findViewById<View>(R.id.info) as TextView).text = data
                val checkBox = holderSingle.itemView.findViewById<CheckBox>(R.id.checkBox)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    checkBox.buttonTintList = ColorStateList.valueOf(CommonManager.getMainColor())
                }
                checkBox.isChecked = mBean.curCheckBox == position
                if (mBean.curCheckBox == position) {
                    mCurBox = checkBox
                }
                checkBox.setOnTouchListener { v, event ->
                    if (event.action == MotionEvent.ACTION_UP) {
                        if (!checkBox.isChecked) {
                            mBean.curCheckBox = holderSingle.layoutPosition
                            checkBox.isChecked = true
                            if (mCurBox != null) {
                                mCurBox!!.isChecked = false
                            }
                            mCurBox = checkBox
                        }
                    }
                    true
                }
            }
        }
    }

    fun setItems(items: Array<String>?): CheckBoxDialog {
        mBean.items = items
        return this
    }

    fun setTitle(title: String): CheckBoxDialog {
        mBean.title = title
        return this
    }

    fun setCallBack(onItemClickListener: DialogInterface.OnClickListener): CheckBoxDialog {
        mBean.mOnItemClickListener = onItemClickListener
        return this
    }

    private inner class Bean {
        internal var title: String? = null
        internal var items: Array<String>? = null
        internal var curCheckBox: Int = 0
        internal var mOnItemClickListener: DialogInterface.OnClickListener? = null
    }
}