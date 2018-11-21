package com.mdzz.run

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers

class HKThrowable {
    fun handleLoadPackage() {
        XposedHelpers.findAndHookMethod(Throwable::class.java, "getStackTrace",
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        param.result = emptyArray<StackTraceElement>()
                    }
                })
        XposedBridge.log("run: 模块1工作正常")
    }
}