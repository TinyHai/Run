package com.mdzz.hook.base

import com.mdzz.hook.LOG_SWICH
import com.mdzz.hook.util.LogUtil.saveInfo
import com.mdzz.hook.util.XSharedPrefUtil

abstract class BaseHook {

    var number = 0

    abstract fun beginHook()

    companion object {

        private const val TAG = "BaseHook"

        private val mOpenLog: Boolean by lazy {
            XSharedPrefUtil.getBoolean(LOG_SWICH)
        }

        lateinit var processName: String

        @JvmStatic
        lateinit var classLoader: ClassLoader

        fun log(tag: String, msg: String) {
            if (mOpenLog) {
                saveInfo(tag, msg)
            }
        }

        fun log(tag: String, th: Throwable) {
            if (mOpenLog) {
                saveInfo(tag, th)
            }
        }
    }
}