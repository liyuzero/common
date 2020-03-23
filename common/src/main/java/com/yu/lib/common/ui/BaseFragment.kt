package com.yu.lib.common.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.yu.lib.common.data.DataModel
import com.yu.lib.common.data.DataRequest

abstract class BaseFragment: Fragment() {
    private lateinit var mDataModel: DataModel
    private lateinit var mRootView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataModel = DataModel(this, this);
    }

    fun<T> execute(callbackViewBindMethod: String, dataRequest: DataRequest<T>) {
        mDataModel.execute(callbackViewBindMethod, dataRequest)
    }

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mRootView = inflater.inflate(getLayoutRes(), null)
        return mRootView
    }

    final override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadView(view)
    }

    @Suppress("UNCHECKED_CAST")
    fun<T> find(id: Int): T {
        return mRootView.findViewById<View>(id) as T;
    }

    @LayoutRes
    abstract fun getLayoutRes(): Int;
    abstract fun loadView(view: View)

    fun setHostListener(hostActivityListener: HostActivity.HostActivityListener) {
        val hostActivity: HostActivity = activity as HostActivity
        hostActivity.mHostActivityListener = hostActivityListener
    }
}