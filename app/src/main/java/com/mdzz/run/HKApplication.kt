package com.mdzz.run

import android.util.Log
import com.mdzz.run.base.BaseHook
import com.mdzz.run.util.LogUtil
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers

class HKApplication : BaseHook() {

    companion object {
        private const val TAG = "HKApplication"
    }

    override fun beginHook() {
        XposedHelpers.findAndHookMethod(HOOK_APPLICATION, classLoader, "onCreate", OnCreateMethodHook)
        XposedHelpers.findAndHookMethod(HOOK_APPLICATION, classLoader, "onTerminate",OnTerminateMethodHook)
        log(TAG, "run: 模块0工作正常")
    }

    object OnCreateMethodHook : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam?) {
            Thread.setDefaultUncaughtExceptionHandler { th, ex ->
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