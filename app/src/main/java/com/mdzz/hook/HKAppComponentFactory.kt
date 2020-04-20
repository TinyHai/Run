package com.mdzz.hook

import android.os.Build
import com.mdzz.BuildConfig
import com.mdzz.hook.base.BaseHook
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import java.lang.Exception

/**
 * author: tiny
 * created on: 20-4-20 下午9:45
 */
class HKAppComponentFactory : BaseHook() {

    companion object {
        private const val TAG = "HKAppComponentFactory"
    }

    override fun beginHook() {
        if (BuildConfig.DEBUG.not()) {
            return
        }
        if (Build.VERSION.SDK_INT < 28) {
            return
        }
        val clazz = try {
            classLoader.loadClass("s.h.e.l.l.A")
        } catch (e: Exception) {
            log(TAG, e)
            null
        } ?: return

        val declareMethods = clazz.declaredMethods
        declareMethods.forEach {
            XposedBridge.hookMethod(it, MyMethodHook)
        }
    }

    object MyMethodHook : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam?) {
            param?.let {
                log(TAG, "begin: +++++++++++++++++++")
                log(TAG, "MethodName = ${it.method.name}")
                log(TAG, "args = ${it.args?.contentToString()}")
                log(TAG, "return = ${it.resultOrThrowable}")
                log(TAG, "end: +++++++++++++++++++++")
            }
        }
    }
}