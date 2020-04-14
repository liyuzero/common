package com.yu.lib.common.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object JsonUtils {

    fun format(obj: Any): String {
        return Gson().toJson(obj)
    }

    fun<T> parse(json: String, clazz: Class<T>): T {
        return Gson().fromJson(json, clazz)
    }

    fun<T> parseList(json: String): MutableList<T> {
        val typeToken = object : TypeToken<List<T>>() {}.type;
        return Gson().fromJson(json, typeToken)
    }

}