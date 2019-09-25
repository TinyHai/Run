package com.mdzz.run

import com.mdzz.run.base.BaseHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers

class HKThrowable : BaseHook() {

    companion object {
        private const val TAG = "HKThrowable"
    }

    override fun beginHook() {
        XposedHelpers.findAndHookMethod("java.lang.Throwable", classLoader, "getStackTrace",
                XC_MethodReplacement.returnConstant(emptyArray<StackTraceElement>()))
        log(TAG, "run: 模块1工作正常")
    }
}