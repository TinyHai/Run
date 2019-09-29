package com.mdzz.run

import android.util.Log
import com.mdzz.run.base.BaseHook
import com.mdzz.run.util.LogUtil
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import java.lang.reflect.Method

class HKApplication : BaseHook() {

    companion object {
        private const val TAG = "HKApplication"
    }

    override fun beginHook() {
        try {
            val myApplicationClass = XposedHelpers.findClass(HOOK_APPLICATION, classLoader)
            XposedHelpers.findAndHookMethod(myApplicationClass, "onCreate", OnCreateMethodHook)
            XposedBridge.hookMethod(getOnTerminateMethod(myApplicationClass), OnTerminateMethodHook)
            log(TAG, "run: 模块0工作正常")
        } catch (e: XposedHelpers.ClassNotFoundError) {
            log(TAG, "run: 模块0出错")
            log(TAG, e)
        } catch (th: Throwable) {
            log(TAG, "run: 模块0出错")
            log(TAG, th)
        }
    }

    private fun getOnTerminateMethod(myApplicationClass: Class<*>): Method? {
        return try {
            val onTerminate = myApplicationClass.getMethod("onTerminate")
            if (!onTerminate.isAccessible) {
                onTerminate.isAccessible = true
            }
            onTerminate
        } catch (e: NoSuchMethodException) {
            log(TAG, e)
            null
        }
    }

    object OnCreateMethodHook : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam?) {
            Thread.currentThread().setUncaughtExceptionHandler { th, ex ->
                log(TAG, th.name)
                log(TAG, ex)
            }
        }
    }

    object OnTerminateMethodHook : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam?) {
            try {
                LogUtil.closeThreadPool()
            } catch (th: Throwable) {
                Log.e(TAG, "LogUtil close ", th)
            }
        }
    }
}