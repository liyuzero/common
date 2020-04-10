package com.yu.lib.common.utils

import android.content.Context
import android.widget.Toast

object ToastUtil {

    fun showToast(context: Context, info: String) {
        Toast.makeText(
            context,
            info,
            Toast.LENGTH_SHORT
        ).show()
    }

    fun showToast(context: Context, info: String, duration: Int) {
        val toast = Toast.makeText(
            context,
            info,
            Toast.LENGTH_LONG
        )
        toast.duration = duration
        toast.show()
    }
}