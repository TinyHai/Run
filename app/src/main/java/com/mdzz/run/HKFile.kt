package com.mdzz.run

import com.mdzz.run.base.BaseHook
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import java.lang.NullPointerException

class HKFile : BaseHook() {

    companion object {
        private const val TAG = "HKFile"
    }

    override fun beginHook() {
        val clazz = try {
            classLoader.loadClass("java.io.FileReader")
        } catch (th: Throwable) {
            log(TAG, th)
            null
        }
        XposedHelpers.findAndHookConstructor(clazz, "java.lang.String", MyMethodHook)
        log(TAG, "run: 模块3工作正常")
    }

    private object MyMethodHook : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam) {
            if (param.args[0].toString().startsWith("/proc/")) {
                param.throwable = NullPointerException("nmsl")
            }
        }
    }
}