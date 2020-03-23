package com.yu.lib.common.utils

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.yu.lib.common.R


fun loadPic(imageView: ImageView, url: Any) {
    if(url != imageView.tag) {
        val drawableCrossFadeFactory = DrawableCrossFadeFactory.Builder(300).setCrossFadeEnabled(true).build()
        Glide.with(imageView.context).load(url)
            .centerCrop()
            .placeholder(R.drawable.common_pic_default)
            .transition(DrawableTransitionOptions.with(drawableCrossFadeFactory))
            .into(imageView)
    }
}