package com.mdzz.run

import com.mdzz.run.base.BaseHook
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers

class HKMethod : BaseHook() {

    companion object {
        private const val TAG = "HKMethod"
    }

    override fun beginHook() {
        val methodClass = try {
            Class.forName("java.lang.reflect.Modifier")
        } catch (th: Throwable) {
            log(TAG, th)
            return
        }
        XposedHelpers.findAndHookMethod(methodClass, "isNative",
                Int::class.javaPrimitiveType, MyMethodHook)
        log(TAG, "run: 模块5工作正常")
    }

    object MyMethodHook : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam) {
            log(TAG, "isNative be false")
            param.result = false
        }
    }
}