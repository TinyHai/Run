package com.mdzz.run

import com.mdzz.run.base.BaseHook
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers

class HKApplication : BaseHook() {

    companion object {
        private const val TAG = "HKApplication"
    }

    override fun beginHook() {
        XposedHelpers.findAndHookMethod(HOOK_APPLICATION, classLoader, "onCreate", MyMethodHook)
        log(TAG, "run: 模块0工作正常")
    }

    object MyMethodHook : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam?) {
            val currentThread = Thread.currentThread()
            currentThread.setUncaughtExceptionHandler { th, ex ->
                log(TAG, ex)
            }
        }
    }
}