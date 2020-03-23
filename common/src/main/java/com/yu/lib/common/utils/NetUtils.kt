package com.yu.lib.common.utils

import android.content.Context
import android.net.ConnectivityManager


fun isNetWorkConnected(context: Context): Boolean {
    //获取连接活动管理器
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    //获取链接网络信息
    val networkInfo = connectivityManager.activeNetworkInfo

    return networkInfo != null && networkInfo.isAvailable

}