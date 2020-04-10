package com.yu.lib.common.ui.adapter.multi;

import android.content.Context;
import android.view.View;

public abstract class BaseItemBinder<T> {
    private String mViewTypeStr;
    int viewType;

    public String getViewTypeStr() {
        return mViewTypeStr;
    }

    void setViewTypeStr(String viewTypeStr) {
        mViewTypeStr = viewTypeStr;
    }

    public abstract int getLayoutRes();

    public View getView(Context context, int viewType) {
        return null;
    }

    public void onCreateView(MultiRecyclerAdapter.BaseViewHolder holder) {

    }

    public abstract void initBindData(MultiRecyclerAdapter.BaseViewHolder holder, int position, T t);

    public void bindData(MultiRecyclerAdapter.BaseViewHolder holder, int position, T t) {
    }

    public void onViewRecycled(MultiRecyclerAdapter.BaseViewHolder holder) {
    }

    public void onViewAttachedToWindow(MultiRecyclerAdapter.BaseViewHolder holder) {
    }

    public void onViewDetachedFromWindow(MultiRecyclerAdapter.BaseViewHolder holder) {
    }
}
