package com.yu.lib.common.utils

import android.os.Build
import android.text.Html
import android.widget.TextView

fun showHtml(textView: TextView, source: String) {
    val html = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY)
    } else {
        Html.fromHtml(source)
    }
    textView.text = html
}