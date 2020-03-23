package com.yu.lib.common.data

import android.app.Activity
import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import java.lang.reflect.Method
import java.util.HashMap
import java.util.concurrent.Executors

class DataModel(fragment: Fragment, bindView: Any) {
    private var mBindViewMethodMap: HashMap<String, Method> = HashMap()
    private var mDataViewModel: DataViewModel

    init {
        initMethods(bindView)
        mDataViewModel = ViewModelProviders.of(fragment)
            .get(DataViewModel::class.java)
        mDataViewModel.mData.observe(fragment, Observer {
            val method = mBindViewMethodMap[it.mBindViewStr]
            method?.isAccessible = true
            method?.invoke(bindView, it)
        })
    }

    private fun initMethods(mBindView: Any) {
        val methods = mBindView.javaClass.declaredMethods
        for (method in methods) {
            val viewBind = method.getAnnotation(ViewBind::class.java)
            if (viewBind != null) {
                mBindViewMethodMap[viewBind.value] = method
            }
        }
    }

    fun<T> execute(callbackViewBindMethod: String, dataRequest: DataRequest<T>) {
        mDataViewModel.execute(callbackViewBindMethod, dataRequest)
    }

}

class DataViewModel : ViewModel() {
    private val mRequestData = MutableLiveData<RequestBean<*>>()
    var mData: LiveData<Response<*>>

    init {
        mData = Transformations.switchMap(mRequestData) {
            val data = MutableLiveData<Response<*>>()
            ThreadPool.postOnIO(Runnable {
                val res = getResponse(it.callbackViewBindMethod, it.dataRequest)
                ThreadPool.postOnUI(Runnable {
                    data.value = res
                })
            })
            data
        }
    }

    private data class RequestBean<T>(val callbackViewBindMethod: String, val dataRequest: DataRequest<T>)

    fun <T> execute(callbackViewBindMethod: String, dataRequest: DataRequest<T>) {
        mRequestData.value = RequestBean(callbackViewBindMethod, dataRequest)
    }

    private fun <T> getResponse(callbackViewBindMethod: String, dataRequest: DataRequest<T>): Response<T> {
        val response = Response<T>()
        try {
            response.mBindViewStr = callbackViewBindMethod
            response.mData = dataRequest.requestData()
        } catch (e: Exception) {
            e.printStackTrace()
            response.mException = e
        }
        return response
    }
}

private class ViewModelProviders {

    companion object {
        @MainThread
        fun of(fragment: Fragment): ViewModelProvider {
            val factory = ViewModelProvider.AndroidViewModelFactory.getInstance(
                checkApplication(
                    checkActivity(
                        fragment
                    )
                )
            )
            return ViewModelProvider(fragment.viewModelStore, factory)
        }

        @MainThread
        fun of(activity: FragmentActivity): ViewModelProvider {
            val factory = ViewModelProvider.AndroidViewModelFactory.getInstance(
                checkApplication(activity)
            )
            return ViewModelProvider(activity.viewModelStore, factory)
        }

        private fun checkApplication(activity: Activity): Application {
            return activity.application
                ?: throw IllegalStateException("Your activity/fragment is not yet attached to " + "Application. You can't requestForHtml ViewModel before onCreate call.")
        }

        private fun checkActivity(fragment: Fragment): Activity {
            return fragment.activity
                ?: throw IllegalStateException("Can't create ViewModelProvider for detached fragment")
        }
    }

    @MainThread
    fun of(fragment: Fragment, factory: ViewModelProvider.Factory): ViewModelProvider {
        checkApplication(
            checkActivity(
                fragment
            )
        )
        return ViewModelProvider(fragment.viewModelStore, factory)
    }

    @MainThread
    fun of(
        activity: FragmentActivity,
        factory: ViewModelProvider.Factory
    ): ViewModelProvider {
        checkApplication(activity)
        return ViewModelProvider(activity.viewModelStore, factory)
    }
}

object ThreadPool {
    private val executorService = Executors.newFixedThreadPool(3)
    private val mMainHandler: Handler = Handler(Looper.getMainLooper())

    fun postOnUI(runnable: Runnable, delayTime: Long) {
        mMainHandler.postDelayed(runnable, delayTime)
    }

    fun postOnUI(runnable: Runnable) {
        mMainHandler.post(runnable)
    }

    fun postOnIO(runnable: Runnable) {
        executorService.execute(runnable)
    }
}