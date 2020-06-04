package com.mdzz.hook

import com.mdzz.hook.base.BaseHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers

class HKThrowable : BaseHook() {

    companion object {
        private const val TAG = "HKThrowable"
    }

    override fun beginHook() {
        try {
            XposedHelpers.findAndHookMethod("java.lang.Throwable", classLoader, "getStackTrace",
                    XC_MethodReplacement.returnConstant(emptyArray<StackTraceElement>()))
            log(TAG, "run: 模块1工作正常")
        } catch (th: Throwable) {
            log(TAG, "run: 模块1出错")
            log(TAG, th)
        }
    }
}