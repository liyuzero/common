package com.yu.lib.common.data

interface DataRequest<T> {
    fun requestData() : T?
}

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ViewBind(val value: String)