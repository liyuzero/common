package com.yu.lib.common.utils

import android.app.Activity
import android.content.Context.INPUT_METHOD_SERVICE
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

fun showSoftInput(et: EditText) {
    et.requestFocus()
    et.postDelayed(Runnable {
        val imm = et.context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(et, 0)
    }, 50)
}

fun closeSoftInput(editText: EditText) {
    //拿到InputMethodManager
    val imm = editText.context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    //如果window上view获取焦点 && view不为空
    val v = (editText.context as Activity).window.peekDecorView()
    if (null != v) {
        imm.hideSoftInputFromWindow(v.windowToken, 0)
    }
}