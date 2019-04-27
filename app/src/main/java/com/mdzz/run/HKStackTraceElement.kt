package com.mdzz.run

import com.mdzz.run.base.BaseHook
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers

class HKStackTraceElement : BaseHook() {

    companion object {
        private const val TAG = "HKStackTraceElement"
    }

    override fun beginHook() {
        XposedHelpers.findAndHookMethod("java.lang.StackTraceElement", classLoader, "getClassName",
                MyMethodHook)
        log(TAG, "run: 模块1工作正常")
    }

    private object MyMethodHook : XC_MethodHook() {

        private val stringSet = setOf("com.android.internal.os.ZygoteInit", "de.robv.android.xposed.XposedBridge")

        override fun afterHookedMethod(param: MethodHookParam) {
            if (param.result in stringSet) {
                param.result = "android.os.nmsl"
            }
        }
    }
}