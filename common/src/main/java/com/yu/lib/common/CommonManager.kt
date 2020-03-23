package com.yu.lib.common

import android.content.Context
import androidx.core.content.ContextCompat
import com.tencent.mmkv.MMKV

class CommonManager {

    companion object {
        private var mMMKV: MMKV? = null
        private var mMainColor: Int? = null

        fun init(context: Context) {
            MMKV.initialize(context)
            mMMKV = MMKV.defaultMMKV()
            mMainColor = mMMKV!!.decodeInt("mainColor", ContextCompat.getColor(context, R.color.common_main))
        }

        fun setMainColor(mainColor: Int) {
            this.mMainColor = mainColor
            mMMKV!!.encode("mainColor", mainColor)
        }

        fun getMainColor(): Int {
            return mMainColor!!
        }
    }
}