package com.yu.lib.common.data

import java.lang.Exception

class Response<T> {
    var mData: T? = null
    var mException: Exception? = null
    lateinit var mBindViewStr: String
}