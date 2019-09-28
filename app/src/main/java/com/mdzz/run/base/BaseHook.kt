package com.mdzz.run.base

import com.mdzz.run.LOG_SWICH
import com.mdzz.run.util.LogUtil.saveInfo
import com.mdzz.run.util.XSharedPrefUtil

abstract class BaseHook {

    abstract fun beginHook()

    companion object {
        private val mOpenLog: Boolean
            get() = XSharedPrefUtil.getBoolean(LOG_SWICH)

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