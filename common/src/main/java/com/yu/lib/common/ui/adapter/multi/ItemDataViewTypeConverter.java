package com.yu.lib.common.ui.adapter.multi;

public interface ItemDataViewTypeConverter<T> {
    String getViewType(int pos, T t);
}
