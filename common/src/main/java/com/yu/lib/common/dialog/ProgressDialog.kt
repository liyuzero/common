package com.yu.lib.common.dialog

import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import com.yu.lib.common.CommonManager
import com.yu.lib.common.R

class DialogProgress(context: Context) : Dialog(context, R.style.dialog) {
    private var view: TextView? = null

    override fun onCreate(paramBundle: Bundle) {
        super.onCreate(paramBundle)
        setContentView(R.layout.common_dialog_progress)

        view = findViewById(R.id.text)
        val progressBar = findViewById<ProgressBar>(R.id.progress)
        progressBar.isIndeterminate = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val mainColor = CommonManager.getMainColor()
            progressBar.indeterminateTintList = ColorStateList.valueOf(mainColor)
            progressBar.progressTintList = ColorStateList.valueOf(mainColor)
        }
    }

    fun setText(str: String) {
        view!!.text = str
    }

}